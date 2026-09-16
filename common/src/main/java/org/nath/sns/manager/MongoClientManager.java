package org.nath.sns.manager;

import com.mongodb.client.MongoClient;
import io.dropwizard.lifecycle.Managed;

public class MongoClientManager implements Managed {
    private final MongoClient mongoClient;

    public MongoClientManager(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void start() throws Exception {
        // The driver connects lazily; no startup logic needed
    }

    @Override
    public void stop() throws Exception {
        mongoClient.close();
    }
}