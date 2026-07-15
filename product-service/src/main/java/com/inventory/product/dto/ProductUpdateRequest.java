package com.inventory.product.dto;

import com.inventory.product.enums.Unit;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductUpdateRequest(

        @Schema(example = "Tibet Product Updated")
        @NotBlank

        String name,

        @Schema(example = "ME001")
        @NotBlank

        @Size(min = 5, max = 5)

        String code,

        @Schema(example = "ME")
        @NotBlank

        @Size(min = 2, max = 2)

        String categoryCode,

        @Schema(example = "Migros")
        @NotBlank

        String brand,

        @Schema(example = "PIECE")
        @NotNull

        Unit unit

) {

}
