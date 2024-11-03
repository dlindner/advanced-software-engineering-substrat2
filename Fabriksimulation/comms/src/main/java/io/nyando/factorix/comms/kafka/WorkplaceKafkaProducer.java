package io.nyando.factorix.comms.kafka;

import com.google.gson.Gson;
import io.nyando.factorix.model.work.Workplace;
import io.nyando.factorix.services.events.*;
import io.nyando.factorix.services.interfaces.EventBroker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WorkplaceKafkaProducer implements OrderCreatedListener, ProcessStartedListener, ProcessCompletedListener {

    private final static Logger log = LoggerFactory.getLogger(WorkplaceKafkaProducer.class);

    private final String workplaceTopic;
    private final KafkaProducer<String, String> producer;

    public WorkplaceKafkaProducer(String kafkaHost, Workplace workplace, EventBroker broker) throws IOException {
        this.workplaceTopic = String.format("%%s-workplace-%s", workplace.getWorkplaceID());
        this.producer = createProducer(kafkaHost);

        broker.register(this, OrderCreated.class);
        broker.register(this, ProcessStarted.class);
        broker.register(this, ProcessCompleted.class);
    }

    @Override
    public void onOrderCreated(OrderCreated event) {
        String value = new Gson().toJson(event, OrderCreated.class);
        this.sendEvent(String.format(this.workplaceTopic, "order-created"), value);
    }

    @Override
    public void onProcessStarted(ProcessStarted event) {
        String value = new Gson().toJson(event, ProcessStarted.class);
        this.sendEvent(String.format(this.workplaceTopic, "process-started"), value);
    }

    @Override
    public void onProcessCompleted(ProcessCompleted event) {
        String value = new Gson().toJson(event, ProcessCompleted.class);
        this.sendEvent(String.format(this.workplaceTopic, "process-complete"), value);
    }

    private void sendEvent(String topic, String value) {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        try {
            RecordMetadata metadata = this.producer.send(record).get();
            log.debug("successfully sent event: partition {}, offset {}", metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException ex) {
            log.error("sending kafka event failed: {}", ex.getMessage());
        }
    }

    private KafkaProducer<String, String> createProducer(String kafkaHost) throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("config.properties"));
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        return new KafkaProducer<>(props);
    }

}
