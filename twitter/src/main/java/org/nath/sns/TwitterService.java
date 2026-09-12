package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkApplicationListener;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.nath.sns.config.TwitterModule;
import org.nath.sns.config.TwitterServiceConfiguration;
import org.nath.sns.dto.AuthenticatedUser;
import org.nath.sns.entity.TweetEntity;
import org.nath.sns.resource.TwitterServiceHealthResource;
import org.nath.sns.util.JwtAuthenticatorUtil;
import org.nath.sns.util.RoleAuthorizerUtil;
import org.nath.sns.util.UserContextFilter;


public class TwitterService extends Application<TwitterServiceConfiguration>
{
    private final HibernateBundle<TwitterServiceConfiguration> hibernateBundle =
            new HibernateBundle<TwitterServiceConfiguration>(TweetEntity.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TwitterServiceConfiguration config) {
                    return config.getDatabase();
                }
            };

    @Override
    public void initialize(Bootstrap<TwitterServiceConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new FlywayBundle<TwitterServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TwitterServiceConfiguration configuration) {
                return configuration.getDatabase();
            }

            @Override
            public io.dropwizard.flyway.FlywayFactory getFlywayFactory(TwitterServiceConfiguration configuration) {
                return configuration.getFlyway();
            }
        });
    }

    public void main(String[] args) throws Exception {
        new TwitterService().run(args);
    }

    public void run(TwitterServiceConfiguration configuration, io.dropwizard.core.setup.Environment environment) {
        Injector injector = Guice.createInjector(
                new TwitterModule(hibernateBundle.getSessionFactory(), configuration)
        );

        // Register UnitOfWork listener for Hibernate session management
        environment.jersey().register(new UnitOfWorkApplicationListener());
        // Register UserContextFilter to capture Authenticated User
        environment.jersey().register(new UserContextFilter());

        // Run Flyway migrations automatically on startup
        Flyway flyway = configuration.getFlyway()
                .build(configuration.getDatabase().build(environment.metrics(), "flyway"));
        flyway.migrate();

        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(injector.getInstance(JwtAuthenticatorUtil.class))
                        .setAuthorizer(injector.getInstance(RoleAuthorizerUtil.class))
                        .setPrefix("Bearer")
                        .setRealm("API_SECURITY")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        environment.jersey().register(injector.getInstance(TwitterServiceHealthResource.class));

    }
}
