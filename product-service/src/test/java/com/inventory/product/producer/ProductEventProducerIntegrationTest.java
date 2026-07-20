package com.inventory.product.producer;

import com.inventory.product.event.ProductCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@EmbeddedKafka(partitions = 1, topics = ProductEventProducerIntegrationTest.TOPIC)
class ProductEventProducerIntegrationTest {

    static final String TOPIC = "product-created";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void sendProductCreatedPublishesEventToKafka() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerProps)
        );
        ProductEventProducer producer = new ProductEventProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "productCreatedTopic", TOPIC);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("product-producer-test", "true", embeddedKafka);
        JsonDeserializer<ProductCreatedEvent> valueDeserializer = new JsonDeserializer<>(ProductCreatedEvent.class);
        valueDeserializer.addTrustedPackages("com.inventory.product.event");

        try (Consumer<String, ProductCreatedEvent> consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                valueDeserializer
        ).createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, TOPIC);

            producer.sendProductCreated(event());

            ConsumerRecord<String, ProductCreatedEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
            assertThat(record.key()).isEqualTo("1");
            assertThat(record.value().productCode()).isEqualTo("ME001");
        }
    }

    private ProductCreatedEvent event() {
        return new ProductCreatedEvent(
                "event-id",
                "PRODUCT_CREATED",
                1,
                Instant.parse("2026-07-17T12:00:00Z"),
                1L,
                "ME001",
                "Meyve Suyu",
                "ME",
                "Test Brand",
                "PIECE"
        );
    }
}
