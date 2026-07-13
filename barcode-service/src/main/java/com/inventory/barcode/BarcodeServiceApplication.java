package com.inventory.barcode;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFeignClients
@SpringBootApplication
public class BarcodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarcodeServiceApplication.class, args);
	}

}
