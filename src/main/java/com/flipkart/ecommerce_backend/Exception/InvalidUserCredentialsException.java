package com.flipkart.ecommerce_backend.Exception;

public class InvalidUserCredentialsException extends Exception {

  private String message;

  public InvalidUserCredentialsException(String message) {
    this.message = message;
  }
}
