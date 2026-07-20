package com.inventory.product.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.product.entity.Product;
import com.inventory.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.product-created}")
    private String productCreatedTopic;

    @Value("${app.kafka.outbox.max-attempts:3}")
    private int maxAttempts;

    @Transactional
    public void saveProductCreatedEvent(Product product) {
        ProductCreatedEvent event = ProductCreatedEvent.from(product);

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("PRODUCT")
                    .aggregateId(product.getId())
                    .eventType(event.eventType())
                    .topic(productCreatedTopic)
                    .messageKey(product.getId().toString())
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxEventStatus.PENDING)
                    .attempts(0)
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize ProductCreatedEvent", e);
        }
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> getPublishableEvents() {
        return outboxEventRepository.findTop20ByStatusInAndAttemptsLessThanOrderByCreatedAtAsc(
                List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
                maxAttempts
        );
    }

    @Transactional
    public void markPublished(Long outboxEventId) {
        OutboxEvent event = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + outboxEventId));

        event.setStatus(OutboxEventStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        event.setLastError(null);
    }

    @Transactional
    public void markFailed(Long outboxEventId, Throwable exception) {
        OutboxEvent event = outboxEventRepository.findById(outboxEventId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + outboxEventId));

        event.setAttempts(event.getAttempts() + 1);
        event.setStatus(OutboxEventStatus.FAILED);
        event.setLastError(exception.getMessage());
    }
}
