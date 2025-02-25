package com.flipkart.ecommerce_backend.api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.http.HttpStatus;

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

	public RegistrationResponse(RegistrationStatus status, String email, String message) {
		this.status = status;
		this.message = message != null && !message.isEmpty() ? message : "Registration successful. Please check your email to verify your account.";
		this.email = email;
	}
}
