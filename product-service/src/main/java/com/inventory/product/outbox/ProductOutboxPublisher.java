package com.inventory.product.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.product.event.ProductCreatedEvent;
import com.inventory.product.producer.ProductEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductOutboxPublisher {

    private final ProductOutboxService productOutboxService;
    private final ProductEventProducer productEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.kafka.outbox.publish-delay-ms:5000}")
    public void publishPendingEvents() {
        productOutboxService.getPublishableEvents().forEach(this::publish);
    }

    private void publish(OutboxEvent outboxEvent) {
        try {
            ProductCreatedEvent event = objectMapper.readValue(
                    outboxEvent.getPayload(),
                    ProductCreatedEvent.class
            );

            productEventProducer.sendProductCreated(event).join();
            productOutboxService.markPublished(outboxEvent.getId());
        } catch (JsonProcessingException e) {
            log.error("Could not deserialize outbox event id={}", outboxEvent.getId(), e);
            productOutboxService.markFailed(outboxEvent.getId(), e);
        } catch (CompletionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Could not publish outbox event id={}", outboxEvent.getId(), cause);
            productOutboxService.markFailed(outboxEvent.getId(), cause);
        }
    }
}
