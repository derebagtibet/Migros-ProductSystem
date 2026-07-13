package com.inventory.product.dto;

import com.inventory.product.enums.Unit;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(

        @NotBlank

        String name,

        @NotBlank

        @Size(min = 5, max = 5)

        String code,

        @NotBlank

        @Size(min = 2, max = 2)

        String categoryCode,

        @NotBlank

        String brand,

        @NotNull

        Unit unit

) {

}
