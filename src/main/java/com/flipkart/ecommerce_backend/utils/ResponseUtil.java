package com.flipkart.ecommerce_backend.utils;

import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;

import java.util.Map;

public class ResponseUtil {
    public static GenericResponseBody success(String message, Map<String, Object> details) {
        GenericResponseBody response = new GenericResponseBody();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatus("SUCCESS");
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));
        response.setDetails(details);
        return response;
    }

    public static GenericResponseBody failure(String message, String failureReason) {
        GenericResponseBody response = new GenericResponseBody();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatus("FAILURE");
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));
        response.setFailureReason(failureReason);
        return response;
    }
}
