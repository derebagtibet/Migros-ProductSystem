package com.inventory.barcode.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI barcodeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Barcode Service API")
                        .description("Inventory Management Barcode Microservice")
                        .version("1.0.0"));
    }
}