package com.inventory.product.dto;
import com.inventory.product.enums.Unit;
import java.time.LocalDateTime;
public record ProductResponse(
        Long id,
        String name,
        String code,
        String categoryCode,
        String brand,
        Unit unit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}