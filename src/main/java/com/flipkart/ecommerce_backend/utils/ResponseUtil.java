package com.flipkart.ecommerce_backend.utils;

import com.flipkart.ecommerce_backend.dtos.GenericResponseBodyDto;
import java.util.Map;

public class ResponseUtil {
  public static GenericResponseBodyDto success(String message, Map<String, Object> details) {
    GenericResponseBodyDto response = new GenericResponseBodyDto();
    response.setSuccess(true);
    response.setMessage(message);
    response.setStatus("SUCCESS");
    response.setTimestamp(String.valueOf(System.currentTimeMillis()));
    response.setDetails(details);
    return response;
  }

  public static GenericResponseBodyDto failure(String message, String failureReason) {
    GenericResponseBodyDto response = new GenericResponseBodyDto();
    response.setSuccess(false);
    response.setMessage(message);
    response.setStatus("FAILURE");
    response.setTimestamp(String.valueOf(System.currentTimeMillis()));
    response.setFailureReason(failureReason);
    return response;
  }
}
