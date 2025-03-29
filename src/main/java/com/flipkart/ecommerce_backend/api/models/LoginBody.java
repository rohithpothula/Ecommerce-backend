package com.flipkart.ecommerce_backend.api.models;

import jakarta.validation.constraints.NotBlank;

public record LoginBody (
  @NotBlank(message = "Username cannot be blank")
  String username,

  @NotBlank(message = "Password cannot be blank")
  String password
) {}
