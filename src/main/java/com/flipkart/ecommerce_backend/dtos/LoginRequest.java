package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginRequest(
  @NotBlank(message = "Username cannot be blank")
  String username,

  @NotBlank(message = "Password cannot be blank")
  String password
) {}
