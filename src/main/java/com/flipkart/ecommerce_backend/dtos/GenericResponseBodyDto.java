package com.flipkart.ecommerce_backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GenericResponseBodyDto {

  private boolean success;
  private String message;
  private Map<String, Object> details;
  private String status;
  private String timestamp;
  private String failureReason;
}
