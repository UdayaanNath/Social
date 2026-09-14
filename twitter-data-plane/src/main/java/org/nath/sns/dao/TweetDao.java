package org.nath.sns.dao;

import com.mongodb.client.MongoCollection;

public class TweetDao {

    private final MongoCollection<?> tweetCollection;

    public TweetDao(MongoCollection<?> tweetCollection) {
        this.tweetCollection = tweetCollection;
    }
}
