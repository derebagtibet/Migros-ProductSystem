package com.inventory.product.service;

import com.inventory.product.dto.ProductCreateRequest;

import com.inventory.product.dto.ProductResponse;

import com.inventory.product.dto.ProductUpdateRequest;

import com.inventory.product.entity.Product;

import com.inventory.product.exception.ResourceNotFoundException;

import com.inventory.product.feign.BarcodeServiceClient;

import com.inventory.product.mapper.ProductMapper;

import com.inventory.product.repository.ProductRepository;

import com.inventory.product.validation.ProductValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor

public class ProductService {

    private final ProductRepository productRepository;

    private final ProductValidator productValidator;

    private final BarcodeServiceClient barcodeServiceClient;

    public ProductResponse create(ProductCreateRequest request) {

        productValidator.validateForCreate(request);

        Product product = ProductMapper.toEntity(request);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);

    }

    public List<ProductResponse> getAll() {

        return productRepository.findAll()

                .stream()

                .map(ProductMapper::toResponse)

                .toList();

    }

    public ProductResponse getById(Long id) {

        Product product = findProductById(id);

        return ProductMapper.toResponse(product);

    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {

        Product product = findProductById(id);

        productValidator.validateForUpdate(

                id,

                product.getName(),

                product.getCode(),

                request

        );

        ProductMapper.updateEntity(product, request);

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toResponse(updatedProduct);

    }

    public void delete(Long id) {

        Product product = findProductById(id);

        barcodeServiceClient.deleteBarcodesByProductId(id);

        productRepository.delete(product);

    }

    private Product findProductById(Long id) {

        return productRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

    }

}
