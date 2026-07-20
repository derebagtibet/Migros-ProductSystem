package com.inventory.product.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.product.event.ProductCreatedEvent;
import com.inventory.product.producer.ProductEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductOutboxPublisherTest {

    private final ProductOutboxService productOutboxService = mock(ProductOutboxService.class);
    private final ProductEventProducer productEventProducer = mock(ProductEventProducer.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final ProductOutboxPublisher productOutboxPublisher = new ProductOutboxPublisher(
            productOutboxService,
            productEventProducer,
            objectMapper
    );

    @Test
    void publishPendingEventsMarksOutboxEventPublishedWhenKafkaSendSucceeds() throws Exception {
        OutboxEvent outboxEvent = outboxEvent(objectMapper.writeValueAsString(event()));
        when(productOutboxService.getPublishableEvents()).thenReturn(List.of(outboxEvent));
        when(productEventProducer.sendProductCreated(any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult()));

        productOutboxPublisher.publishPendingEvents();

        verify(productEventProducer).sendProductCreated(any());
        verify(productOutboxService).markPublished(1L);
        verify(productOutboxService, never()).markFailed(any(), any());
    }

    @Test
    void publishPendingEventsMarksOutboxEventFailedWhenKafkaSendFails() throws Exception {
        OutboxEvent outboxEvent = outboxEvent(objectMapper.writeValueAsString(event()));
        CompletableFuture<SendResult<String, ProductCreatedEvent>> failedFuture = new CompletableFuture<>();
        RuntimeException exception = new RuntimeException("kafka down");
        failedFuture.completeExceptionally(exception);

        when(productOutboxService.getPublishableEvents()).thenReturn(List.of(outboxEvent));
        when(productEventProducer.sendProductCreated(any())).thenReturn(failedFuture);

        productOutboxPublisher.publishPendingEvents();

        verify(productOutboxService).markFailed(1L, exception);
        verify(productOutboxService, never()).markPublished(any());
    }

    @Test
    void publishPendingEventsMarksOutboxEventFailedWhenPayloadCannotBeDeserialized() {
        OutboxEvent outboxEvent = outboxEvent("{bad-json");
        when(productOutboxService.getPublishableEvents()).thenReturn(List.of(outboxEvent));

        productOutboxPublisher.publishPendingEvents();

        verify(productEventProducer, never()).sendProductCreated(any());
        verify(productOutboxService).markFailed(any(), any());
    }

    private OutboxEvent outboxEvent(String payload) {
        return OutboxEvent.builder()
                .id(1L)
                .payload(payload)
                .build();
    }

    private SendResult<String, ProductCreatedEvent> sendResult() {
        ProducerRecord<String, ProductCreatedEvent> producerRecord = new ProducerRecord<>(
                "product-created",
                "1",
                event()
        );
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("product-created", 0),
                0,
                0,
                0,
                0,
                0
        );

        return new SendResult<>(producerRecord, metadata);
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
