package com.flipkart.ecommerce_backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetBody {

  private String token;
  private String newPassword;

}
