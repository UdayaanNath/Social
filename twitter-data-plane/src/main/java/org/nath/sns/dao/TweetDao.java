package org.nath.sns.dao;

import com.mongodb.client.MongoCollection;
import org.nath.sns.converter.TweetConverter;
import org.nath.sns.entity.TweetEntity;
import org.bson.Document;

public class TweetDao {

    private final MongoCollection<Document> tweetCollection;

    public TweetDao(MongoCollection<Document> tweetCollection) {
        this.tweetCollection = tweetCollection;
    }

    public TweetEntity createTweet(TweetEntity tweetEntity) {
        Document tweet = TweetConverter.toDocument(tweetEntity);
        tweetCollection.insertOne(tweet);
        return TweetConverter.toEntity(tweet);
    }
}
