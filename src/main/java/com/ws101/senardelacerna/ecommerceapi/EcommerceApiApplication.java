package com.ws101.senardelacerna.ecommerceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class EcommerceApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
        System.out.println("========================================");
        System.out.println("E-Commerce API is running!");
        System.out.println("API URL: http://localhost:8080");
        System.out.println("========================================");
    }
}