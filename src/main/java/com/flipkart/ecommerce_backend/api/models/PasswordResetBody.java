package com.flipkart.ecommerce_backend.api.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetBody {

  private String token;
  private String newPassword;

}
