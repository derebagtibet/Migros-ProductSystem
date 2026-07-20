package com.inventory.product.service;

import com.inventory.product.dto.ProductCreateRequest;
import com.inventory.product.entity.Product;
import com.inventory.product.enums.Unit;
import com.inventory.product.exception.BusinessException;
import com.inventory.product.feign.BarcodeServiceClient;
import com.inventory.product.outbox.ProductOutboxService;
import com.inventory.product.repository.ProductRepository;
import com.inventory.product.validation.ProductValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductValidator productValidator;

    @Mock
    private BarcodeServiceClient barcodeServiceClient;

    @Mock
    private ProductOutboxService productOutboxService;

    @InjectMocks
    private ProductService productService;

    @Test
    void createStoresProductCreatedEventInOutboxAfterProductIsSaved() {
        ProductCreateRequest request = request();
        Product savedProduct = savedProduct();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        var response = productService.create(request);

        assertEquals(savedProduct.getId(), response.id());
        verify(productValidator).validateForCreate(request);
        verify(productRepository).save(any(Product.class));
        verify(productOutboxService).saveProductCreatedEvent(savedProduct);
    }

    @Test
    void createDoesNotStoreOutboxEventWhenRepositorySaveFails() {
        ProductCreateRequest request = request();
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("database error"));

        assertThrows(RuntimeException.class, () -> productService.create(request));

        verify(productOutboxService, never()).saveProductCreatedEvent(any());
    }

    @Test
    void createDoesNotStoreOutboxEventWhenValidationFails() {
        ProductCreateRequest request = request();
        doThrow(new BusinessException("invalid product")).when(productValidator).validateForCreate(request);

        assertThrows(BusinessException.class, () -> productService.create(request));

        verify(productRepository, never()).save(any(Product.class));
        verify(productOutboxService, never()).saveProductCreatedEvent(any());
    }

    private ProductCreateRequest request() {
        return new ProductCreateRequest("Meyve Suyu", "ME001", "ME", "Test Brand", Unit.PIECE);
    }

    private Product savedProduct() {
        return Product.builder()
                .id(1L)
                .name("Meyve Suyu")
                .code("ME001")
                .categoryCode("ME")
                .brand("Test Brand")
                .unit(Unit.PIECE)
                .build();
    }
}
