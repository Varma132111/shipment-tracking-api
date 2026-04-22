package com.logistics.shipmenttracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ShipmentTrackingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShipmentTrackingApiApplication.class, args);
    }
}
