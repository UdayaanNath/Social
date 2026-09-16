package org.nath.sns.manager;

import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.nath.sns.entity.TweetEntity;
import org.nath.sns.service.TweetMetadataService;
import org.nath.sns.service.TweetService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KafkaTweetsConsumerManager implements Managed {
    private final Consumer<Long, String> consumer;
    private final ExecutorService executorService;
    private final TweetService tweetService;
    private final TweetMetadataService tweetMetadataService;

    public KafkaTweetsConsumerManager(String bootstrapServers, TweetService tweetService, TweetMetadataService tweetMetadataService) {
        this.tweetService = tweetService;
        this.tweetMetadataService = tweetMetadataService;

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "twitter-dp-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        this.consumer = new KafkaConsumer<>(props);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void start() throws Exception {
        executorService.submit(() -> {
            try {
                consumer.subscribe(Collections.singletonList("tweets-topic"));
                while (true) {
                    ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<Long, String> record : records) {
                        try {
                            TweetEntity tweetEntity = tweetService.createTweet(record.value(), record.key());
                            tweetMetadataService.createTweetMetadata(tweetEntity);
                        } catch (Exception e) {
                            log.error("Failed to process tweet from kafka. key={}, offset={}",
                                    record.key(), record.offset(), e);
                        }
                    }
                }
            } catch (WakeupException e) {
                // Ignore exception if closing
            } catch (Exception e) {
                log.error("Error occurred while consuming tweets", e);
            }
            finally {
                consumer.close();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        consumer.wakeup(); // Interrupts the consumer.poll()
        executorService.shutdown();
    }
}
