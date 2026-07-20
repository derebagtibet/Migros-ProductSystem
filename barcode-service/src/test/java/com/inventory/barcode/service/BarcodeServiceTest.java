package com.inventory.barcode.service;

import com.inventory.barcode.client.ProductClient;
import com.inventory.barcode.entity.Barcode;
import com.inventory.barcode.enums.BarcodeType;
import com.inventory.barcode.event.ProductCreatedEvent;
import com.inventory.barcode.exception.BusinessException;
import com.inventory.barcode.generator.BarcodeGeneratorFacade;
import com.inventory.barcode.repository.BarcodeRepository;
import com.inventory.barcode.validation.BarcodeRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceTest {

    @Mock
    private BarcodeRepository barcodeRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private BarcodeRuleValidator barcodeRuleValidator;

    @Mock
    private BarcodeGeneratorFacade barcodeGeneratorFacade;

    @InjectMocks
    private BarcodeService barcodeService;

    @Test
    void createProductBarcodeIfAbsentCreatesBarcodeForProductCreatedEvent() {
        ProductCreatedEvent event = event(1L);
        when(barcodeRepository.existsByProductIdAndType(1L, BarcodeType.PRODUCT)).thenReturn(false);
        when(barcodeGeneratorFacade.generate(BarcodeType.PRODUCT, "ME001", 0)).thenReturn("123456789");
        when(barcodeRepository.save(any(Barcode.class))).thenAnswer(invocation -> {
            Barcode barcode = invocation.getArgument(0);
            barcode.setId(10L);
            return barcode;
        });

        var response = barcodeService.createProductBarcodeIfAbsent(event);

        assertEquals(10L, response.id());
        assertEquals("123456789", response.code());
        verify(barcodeRuleValidator).validate(any(), eq(BarcodeType.PRODUCT));
        verify(barcodeRepository).save(any(Barcode.class));
    }

    @Test
    void createProductBarcodeIfAbsentDoesNotCreateDuplicateProductBarcode() {
        ProductCreatedEvent event = event(1L);
        when(barcodeRepository.existsByProductIdAndType(1L, BarcodeType.PRODUCT)).thenReturn(true);

        var response = barcodeService.createProductBarcodeIfAbsent(event);

        assertNull(response);
        verify(barcodeRuleValidator, never()).validate(any(), eq(BarcodeType.PRODUCT));
        verify(barcodeRepository, never()).save(any(Barcode.class));
    }

    @Test
    void createProductBarcodeIfAbsentThrowsWhenProductIdIsInvalid() {
        ProductCreatedEvent event = event(null);

        assertThrows(BusinessException.class, () -> barcodeService.createProductBarcodeIfAbsent(event));

        verify(barcodeRepository, never()).save(any(Barcode.class));
    }

    private ProductCreatedEvent event(Long productId) {
        return new ProductCreatedEvent(
                "event-id",
                "PRODUCT_CREATED",
                1,
                Instant.parse("2026-07-17T12:00:00Z"),
                productId,
                "ME001",
                "Meyve Suyu",
                "ME",
                "Test Brand",
                "PIECE"
        );
    }
}
