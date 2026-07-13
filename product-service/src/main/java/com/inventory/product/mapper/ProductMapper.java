package com.inventory.product.mapper;

import com.inventory.product.dto.ProductCreateRequest;

import com.inventory.product.dto.ProductResponse;

import com.inventory.product.dto.ProductUpdateRequest;

import com.inventory.product.entity.Product;

public class ProductMapper {

    private ProductMapper() {

    }

    public static Product toEntity(ProductCreateRequest request) {

        return Product.builder()

                .name(request.name())

                .code(request.code())

                .categoryCode(request.categoryCode())

                .brand(request.brand())

                .unit(request.unit())

                .build();

    }

    public static ProductResponse toResponse(Product product) {

        return new ProductResponse(

                product.getId(),

                product.getName(),

                product.getCode(),

                product.getCategoryCode(),

                product.getBrand(),

                product.getUnit(),

                product.getCreatedAt(),

                product.getUpdatedAt()

        );

    }

    public static void updateEntity(Product product, ProductUpdateRequest request) {

        product.setName(request.name());

        product.setCode(request.code());

        product.setCategoryCode(request.categoryCode());

        product.setBrand(request.brand());

        product.setUnit(request.unit());

    }

}
