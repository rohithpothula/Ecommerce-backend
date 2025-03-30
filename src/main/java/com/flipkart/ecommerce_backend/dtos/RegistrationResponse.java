package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistrationResponse {

  public enum RegistrationStatus {
    PENDING_VERIFICATION,
    VERIFIED,
    REGISTRATION_FAILED
  }

  private RegistrationStatus status;
  private String message;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email should be valid")
  private String email;

  public RegistrationResponse(String email) {
    this.status = RegistrationStatus.PENDING_VERIFICATION;
    this.message = "Registration successful. Please check your email to verify your account.";
    this.email = email;
  }
}
