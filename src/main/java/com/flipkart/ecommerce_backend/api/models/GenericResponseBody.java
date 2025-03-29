package com.flipkart.ecommerce_backend.api.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GenericResponseBody {

  private boolean success;
  private String message;
  private Map<String, Object> details;
  private String status;
  private String timestamp;
  private String failureReason;
}
