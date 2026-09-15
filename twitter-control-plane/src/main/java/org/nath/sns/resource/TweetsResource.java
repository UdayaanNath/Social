package org.nath.sns.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.nath.sns.twitter.TweetsApi;
import org.nath.sns.twitter.model.CreateTweetRequest;
import org.nath.sns.twitter.model.SuccessResponse;
import org.nath.sns.twitter.model.Tweet;
import org.nath.sns.twitter.model.UpdateTweetRequest;

import java.util.List;

@Singleton
@Slf4j
public class TweetsResource implements TweetsApi {
    private final Producer<Long, String> producer;
    private final String TOPIC = "tweets-topic";

    @Inject
    public TweetsResource(Producer<Long, String> producer) {
        this.producer = producer;
    }

    @Override
    @UnitOfWork
    @RolesAllowed({"CREATE_TWEET", "ADMIN"})
    public SuccessResponse createTweet(CreateTweetRequest createTweetRequest) {
        ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC,
                createTweetRequest.getAuthorId(), createTweetRequest.getContent());
        // Send asynchronously
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                // Log failure and handle metrics
                log.error("Failed to send record: {} to kafka MQ", record.key());
            }
        });

        return new SuccessResponse().message("Tweet Created");
    }

    @Override
    @UnitOfWork
    @RolesAllowed({"DELETE_TWEET", "ADMIN"})
    public SuccessResponse deleteTweet(String id) {
        return null;
    }

    @Override
    @UnitOfWork
    @RolesAllowed({"READ_TWEET", "ADMIN"})
    public List<Tweet> getAllTweets() {
        return List.of();
    }

    @Override
    @UnitOfWork
    @RolesAllowed({"READ_TWEET", "ADMIN"})
    public Tweet getTweetById(String id) {
        return null;
    }

    @Override
    @UnitOfWork
    @RolesAllowed({"UPDATE_TWEET", "ADMIN"})
    public Tweet updateTweet(String id, UpdateTweetRequest updateTweetRequest) {
        return null;
    }
}
