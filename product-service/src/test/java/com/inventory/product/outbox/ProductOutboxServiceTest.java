package com.inventory.product.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.product.entity.Product;
import com.inventory.product.enums.Unit;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductOutboxServiceTest {

    private final OutboxEventRepository outboxEventRepository = mock(OutboxEventRepository.class);
    private final ProductOutboxService productOutboxService = new ProductOutboxService(
            outboxEventRepository,
            new ObjectMapper().findAndRegisterModules()
    );

    ProductOutboxServiceTest() {
        ReflectionTestUtils.setField(productOutboxService, "productCreatedTopic", "product-created");
        ReflectionTestUtils.setField(productOutboxService, "maxAttempts", 3);
    }

    @Test
    void saveProductCreatedEventStoresPendingOutboxEvent() {
        Product product = product();

        productOutboxService.saveProductCreatedEvent(product);

        verify(outboxEventRepository).save(any(OutboxEvent.class));
    }

    @Test
    void getPublishableEventsReadsPendingAndRetryableFailedEvents() {
        productOutboxService.getPublishableEvents();

        verify(outboxEventRepository)
                .findTop20ByStatusInAndAttemptsLessThanOrderByCreatedAtAsc(
                        List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
                        3
                );
    }

    @Test
    void markPublishedUpdatesStatus() {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(1L)
                .status(OutboxEventStatus.PENDING)
                .lastError("previous error")
                .build();
        when(outboxEventRepository.findById(1L)).thenReturn(Optional.of(outboxEvent));

        productOutboxService.markPublished(1L);

        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(outboxEvent.getPublishedAt()).isNotNull();
        assertThat(outboxEvent.getLastError()).isNull();
    }

    @Test
    void markFailedIncrementsAttemptCount() {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(1L)
                .status(OutboxEventStatus.PENDING)
                .attempts(1)
                .build();
        when(outboxEventRepository.findById(1L)).thenReturn(Optional.of(outboxEvent));

        productOutboxService.markFailed(1L, new RuntimeException("kafka down"));

        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(outboxEvent.getAttempts()).isEqualTo(2);
        assertThat(outboxEvent.getLastError()).isEqualTo("kafka down");
    }

    private Product product() {
        return Product.builder()
                .id(1L)
                .name("Meyve Suyu")
                .code("ME001")
                .categoryCode("ME")
                .brand("Test Brand")
                .unit(Unit.PIECE)
                .build();
    }
}
