package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.hibernate.SessionFactory;
import org.nath.sns.dao.TweetDao;
import org.nath.sns.dao.TweetMetadataDao;
import org.nath.sns.resource.TwitterDataPlaneHealthResource;

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
    public TweetMetadataDao provideTweetMetadataDao() {
        return new TweetMetadataDao(sessionFactory);
    }
}
