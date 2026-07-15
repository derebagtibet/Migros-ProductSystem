package com.inventory.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
        @Schema(example = "ME")
        String code,
        @Schema(example = "Meat/Deli")
        String name,
        @Schema(example = "true")
        boolean active){
}
