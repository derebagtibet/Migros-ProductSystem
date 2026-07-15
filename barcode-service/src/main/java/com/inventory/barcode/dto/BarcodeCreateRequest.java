package com.inventory.barcode.dto;

import com.inventory.barcode.enums.BarcodeType;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record BarcodeCreateRequest(

        @Schema(example = "1")
        @NotNull

        Long productId,

        @Schema(example = "PRODUCT")
        @NotNull

        BarcodeType type

) {

}
