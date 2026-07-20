package com.inventory.product.controller;

import com.inventory.product.dto.ProductCreateRequest;

import com.inventory.product.dto.ProductResponse;

import com.inventory.product.dto.ProductUpdateRequest;

import com.inventory.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/v1/products")

@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;

    @PostMapping

    @ResponseStatus(HttpStatus.CREATED)

    public ProductResponse createProduct(

            @Valid @RequestBody ProductCreateRequest request) {

        return productService.create(request);

    }

    @GetMapping

    public List<ProductResponse> getAllProducts() {

        return productService.getAll();

    }

    @GetMapping("/{id}")

    public ProductResponse getProductById(@PathVariable Long id) {

        return productService.getById(id);

    }

    @PutMapping("/{id}")

    public ProductResponse updateProduct(

            @PathVariable Long id,

            @Valid @RequestBody ProductUpdateRequest request) {

        return productService.update(id, request);

    }

    @DeleteMapping("/{id}")

    @ResponseStatus(HttpStatus.NO_CONTENT)

    @Operation(
            summary = "Delete product and its barcodes",
            description = "Deletes the product record from Product Service and also removes all barcodes linked to this product id."
    )

    public void deleteProduct(@PathVariable Long id) {

        productService.delete(id);

    }

}
