package com.inventory.barcode.dto;

import com.inventory.barcode.enums.BarcodeType;

import java.time.LocalDateTime;

public record BarcodeResponse(
        Long id,
        String code,
        BarcodeType type,
        Long productId,
        LocalDateTime createdAt
) {
}