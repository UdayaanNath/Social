package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;
import org.nath.sns.dao.TweetDao;
import org.nath.sns.dao.TweetMetadataDao;
import org.nath.sns.manager.KafkaTweetsConsumerManager;
import org.nath.sns.resource.TwitterDataPlaneHealthResource;
import org.nath.sns.service.TweetMetadataService;
import org.nath.sns.service.TweetService;

public class TwitterDataPlaneModule extends AbstractModule
{
    private final TwitterDataPlaneConfiguration twitterDataPlaneConfiguration;
    private final SessionFactory sessionFactory;

    public TwitterDataPlaneModule(TwitterDataPlaneConfiguration twitterDataPlaneConfiguration, SessionFactory sessionFactory) {
        this.twitterDataPlaneConfiguration = twitterDataPlaneConfiguration;
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void configure() {
        // Bind your dependencies here
        bind(TwitterDataPlaneConfiguration.class).toInstance(twitterDataPlaneConfiguration);

        bind(TwitterDataPlaneHealthResource.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public MongoClient provideMongoClient() {
        MongoClient mongoClient = MongoClients.create(twitterDataPlaneConfiguration.getMongoConfig().getConnectionString());
        return mongoClient;
    }

    @Provides
    @Singleton
    public MongoDatabase provideMongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(twitterDataPlaneConfiguration.getMongoConfig().getDatabaseName());
    }

    @Provides
    @Singleton
    public TweetDao provideTweetDao(MongoDatabase mongoDatabase) {
        return new TweetDao(mongoDatabase.getCollection("tweets"));
    }

    @Provides
    @Singleton
    public TweetService provideTweetService(TweetDao tweetDao) {
        return new TweetService(tweetDao);
    }

    @Provides
    @Singleton
    public TweetMetadataDao provideTweetMetadataDao() {
        // @UnitOfWork only applies to Jersey requests by default; proxy it so Kafka
        // consumer threads also get a bound Hibernate session + transaction.
        return new UnitOfWorkAwareProxyFactory("hibernate", sessionFactory)
                .create(TweetMetadataDao.class, SessionFactory.class, sessionFactory);
    }

    @Provides
    @Singleton
    public TweetMetadataService provideTweetMetadataService(TweetMetadataDao tweetMetadataDao) {
        return new TweetMetadataService(tweetMetadataDao);
    }

    @Provides
    @Singleton
    public KafkaTweetsConsumerManager provideKafkaTweetsConsumerManager(TweetService tweetService, TweetMetadataService tweetMetadataService) {
        return new KafkaTweetsConsumerManager(twitterDataPlaneConfiguration.getKafka().getBootstrapServers(), tweetService, tweetMetadataService);
    }
}
