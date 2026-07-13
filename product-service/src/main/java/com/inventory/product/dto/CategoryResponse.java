package com.inventory.product.dto;

public record CategoryResponse(
        String code,
        String name,
        boolean active
) {
}
