package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkApplicationListener;
import org.flywaydb.core.Flyway;
import org.nath.sns.config.TwitterDataPlaneConfiguration;
import org.nath.sns.config.TwitterDataPlaneModule;
import org.nath.sns.entity.TweetMetadataEntity;
import org.nath.sns.manager.KafkaTweetsConsumerManager;
import org.nath.sns.manager.MongoClientManager;
import org.nath.sns.resource.MongoHealthCheck;
import org.nath.sns.resource.TwitterDataPlaneHealthResource;

import java.util.ArrayList;
import java.util.List;


public class TwitterDataPlaneService extends Application<TwitterDataPlaneConfiguration>
{
    private final HibernateBundle<TwitterDataPlaneConfiguration> hibernateBundle =
            new HibernateBundle<TwitterDataPlaneConfiguration>(TweetMetadataEntity.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TwitterDataPlaneConfiguration config) {
                    return config.getDatabaseSource();
                }
            };

    public void initialize(Bootstrap<TwitterDataPlaneConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new FlywayBundle<TwitterDataPlaneConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TwitterDataPlaneConfiguration configuration) {
                return configuration.getDatabaseSource();
            }

            @Override
            public io.dropwizard.flyway.FlywayFactory getFlywayFactory(TwitterDataPlaneConfiguration configuration) {
                return configuration.getFlywayFactory();
            }
        });
    }

    public static void main( String[] args ) throws Exception {
        new TwitterDataPlaneService().run(args);
    }

    public void run(TwitterDataPlaneConfiguration configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(
                new TwitterDataPlaneModule(configuration, hibernateBundle.getSessionFactory())
        );

        // Register UnitOfWork listener for Hibernate session management
        environment.jersey().register(new UnitOfWorkApplicationListener());

        // Run Flyway migrations automatically on startup
        Flyway flyway = configuration.getFlywayFactory()
                .build(configuration.getDatabaseSource().build(environment.metrics(), "flyway"));
        flyway.migrate();

        MongoClient mongoClient = injector.getInstance(MongoClient.class);

        // Register the Managed lifecycle for MongoDB (handles clean shutdown)
        environment.lifecycle().manage(new MongoClientManager(mongoClient));

        // Register Health Checks
        environment.healthChecks().register("mongo", new MongoHealthCheck(mongoClient));
        environment.jersey().register(injector.getInstance(TwitterDataPlaneHealthResource.class));

        // Initialize MongoDB collections
        MongoDatabase mongoDatabase = injector.getInstance(MongoDatabase.class);
        intializeCollections(mongoDatabase, configuration);

        // Register the KafkaTweetsConsumerManager to manage the Kafka consumer lifecycle
        environment.lifecycle().manage(injector.getInstance(KafkaTweetsConsumerManager.class));
    }

    public void intializeCollections(MongoDatabase mongoDatabase, TwitterDataPlaneConfiguration configuration) {
        // 1. Get a list of all existing collections
        List<String> existingCollections = new ArrayList<>();
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            existingCollections.add(collectionName);
        }

        // 2. Define the collections your app requires
        List<String> requiredCollections = List.of("tweets");

        // 3. Create them if they don't exist
        for (String required : requiredCollections) {
            if (!existingCollections.contains(required)) {
                mongoDatabase.createCollection(required);
            }
        }

    }
}
