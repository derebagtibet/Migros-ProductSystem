package com.inventory.barcode.consumer;

import com.inventory.barcode.event.ProductCreatedEvent;
import com.inventory.barcode.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedConsumer {

    private final BarcodeService barcodeService;

    @KafkaListener(
            topics = "${app.kafka.topics.product-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ProductCreatedEvent event) {
        log.info(
                "Received product-created event eventId={}, productId={}, productCode={}, productName={}",
                event.eventId(),
                event.productId(),
                event.productCode(),
                event.productName()
        );

        barcodeService.createProductBarcodeIfAbsent(event);
    }
}
