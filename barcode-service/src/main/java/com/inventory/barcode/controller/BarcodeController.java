package com.inventory.barcode.controller;

import com.inventory.barcode.dto.BarcodeCreateRequest;

import com.inventory.barcode.dto.BarcodeResponse;

import com.inventory.barcode.service.BarcodeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/v1/barcodes")

@RequiredArgsConstructor

public class BarcodeController {

    private final BarcodeService barcodeService;

    @PostMapping

    @ResponseStatus(HttpStatus.CREATED)

    public BarcodeResponse createBarcode(@Valid @RequestBody BarcodeCreateRequest request) {

        return barcodeService.create(request);

    }

    @GetMapping

    public List<BarcodeResponse> getAllBarcodes() {

        return barcodeService.getAll();

    }

    @GetMapping("/{id}")

    public BarcodeResponse getBarcodeById(@PathVariable Long id) {

        return barcodeService.getById(id);

    }

    @GetMapping("/product/{productId}")

    public List<BarcodeResponse> getBarcodesByProductId(@PathVariable Long productId) {

        return barcodeService.getByProductId(productId);

    }

    @DeleteMapping("/{id}")

    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void deleteBarcode(@PathVariable Long id) {

        barcodeService.delete(id);

    }

    @DeleteMapping("/product/{productId}")

    @ResponseStatus(HttpStatus.NO_CONTENT)

    @Operation(
            summary = "Delete barcodes by product id",
            description = "Deletes only barcode records linked to the product id. It does not delete the product itself."
    )

    public void deleteBarcodesByProductId(@PathVariable Long productId) {

        barcodeService.deleteByProductId(productId);

    }

}
