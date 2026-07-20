package com.inventory.product.producer;

import com.inventory.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.topics.product-created}")
    private String productCreatedTopic;

    public CompletableFuture<SendResult<String, ProductCreatedEvent>> sendProductCreated(ProductCreatedEvent event) {
        String key = event.productId() != null ? event.productId().toString() : event.productCode();

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send(productCreatedTopic, key, event);

        future
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to publish product-created event for productId={}, productCode={}",
                                event.productId(),
                                event.productCode(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Published product-created event eventId={}, topic={}, partition={}, offset={}",
                            event.eventId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });

        return future;
    }
}
