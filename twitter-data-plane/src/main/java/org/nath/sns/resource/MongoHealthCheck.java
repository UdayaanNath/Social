package org.nath.sns.resource;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.mongodb.client.MongoClient;
import org.bson.Document;

public class MongoHealthCheck extends HealthCheck {
    private final MongoClient mongoClient;

    @Inject
    public MongoHealthCheck(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    protected Result check() throws Exception {
        try {
            // Pings the admin database to verify the connection is alive
            mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy("Cannot connect to MongoDB: " + e.getMessage());
        }
    }
}
