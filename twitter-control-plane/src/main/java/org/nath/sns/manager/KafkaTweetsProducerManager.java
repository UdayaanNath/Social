package org.nath.sns.manager;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import java.util.Properties;

public class KafkaTweetsProducerManager implements Managed {
    private final Producer<Long, String> producer;

    public KafkaTweetsProducerManager(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all"); // High durability for tweets

        this.producer = new KafkaProducer<>(props);
    }

    public Producer<Long, String> getProducer() {
        return producer;
    }

    @Override
    public void start() throws Exception {
        // Initialization if needed
    }

    @Override
    public void stop() throws Exception {
        if (producer != null) {
            producer.close();
        }
    }
}