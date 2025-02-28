package com.flipkart.ecommerce_backend.Exception;

public class UserNotVerifiedException extends Exception {

  private boolean isEmailSent;

  public UserNotVerifiedException(boolean isEmailSent) {
    this.isEmailSent = isEmailSent;
  }

  public boolean isEmailSent() {
    return isEmailSent;
  }

  public void setEmailSent(boolean isEmailSent) {
    this.isEmailSent = isEmailSent;
  }
}
