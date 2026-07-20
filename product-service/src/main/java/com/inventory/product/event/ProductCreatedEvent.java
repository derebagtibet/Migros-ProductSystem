package com.inventory.product.event;

import com.inventory.product.entity.Product;

import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        Long productId,
        String productCode,
        String productName,
        String categoryCode,
        String brand,
        String unit
) {

    public static ProductCreatedEvent from(Product product) {
        return new ProductCreatedEvent(
                UUID.randomUUID().toString(),
                "PRODUCT_CREATED",
                1,
                Instant.now(),
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getCategoryCode(),
                product.getBrand(),
                product.getUnit().name()
        );
    }
}
