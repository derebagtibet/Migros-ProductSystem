package com.inventory.barcode.dto;

import com.inventory.barcode.enums.BarcodeType;

import jakarta.validation.constraints.NotNull;

public record BarcodeCreateRequest(

        @NotNull

        Long productId,

        @NotNull

        BarcodeType type

) {

}
