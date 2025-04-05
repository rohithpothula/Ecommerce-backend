package com.flipkart.ecommerce_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport; // Import

@SpringBootApplication
@EnableSpringDataWebSupport // Enable Spring Data Web Support
public class EcommerceBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcommerceBackendApplication.class, args);
  }
}
