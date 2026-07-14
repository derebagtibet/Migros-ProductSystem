package com.inventory.barcode.generator;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class CaseBarcodeGenerator {

    public String generate() {
        int number = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.valueOf(number);
    }
}
