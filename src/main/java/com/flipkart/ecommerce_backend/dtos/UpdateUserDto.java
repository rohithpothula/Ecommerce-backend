package com.flipkart.ecommerce_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

  private String user_name;
  private String email;
  private String first_name;
  private String last_name;
}
