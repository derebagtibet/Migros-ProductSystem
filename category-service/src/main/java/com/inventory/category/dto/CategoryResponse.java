package com.inventory.category.dto;

public record CategoryResponse(
        String code,
        String name,
        boolean active){
}