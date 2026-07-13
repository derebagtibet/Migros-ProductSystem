package com.inventory.barcode.mapper;

import com.inventory.barcode.dto.BarcodeResponse;

import com.inventory.barcode.entity.Barcode;

public class BarcodeMapper {

    private BarcodeMapper() {

    }

    public static BarcodeResponse toResponse(Barcode barcode) {

        return new BarcodeResponse(

                barcode.getId(),

                barcode.getCode(),

                barcode.getType(),

                barcode.getProductId(),

                barcode.getCreatedAt()

        );

    }

}

