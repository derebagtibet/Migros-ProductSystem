package com.inventory.barcode.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public NewTopic productCreatedDeadLetterTopic(
            @Value("${app.kafka.topics.product-created}") String productCreatedTopic
    ) {
        return TopicBuilder.name(productCreatedTopic + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2L));
        errorHandler.setRetryListeners(this::logRetryFailure);
        return errorHandler;
    }

    private void logRetryFailure(
            ConsumerRecord<?, ?> record,
            Exception exception,
            int deliveryAttempt
    ) {
        log.error(
                "Failed to process Kafka record topic={}, partition={}, offset={}, deliveryAttempt={}",
                record.topic(),
                record.partition(),
                record.offset(),
                deliveryAttempt,
                exception
        );
    }
}
