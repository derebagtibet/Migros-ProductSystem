package com.inventory.barcode.dto;

public record ProductResponse(

        Long id,

        String name,

        String code,

        String categoryCode,

        String brand,

        String unit

) {

}
