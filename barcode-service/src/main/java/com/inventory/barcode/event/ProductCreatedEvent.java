package com.inventory.barcode.event;

import java.time.Instant;

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
}
