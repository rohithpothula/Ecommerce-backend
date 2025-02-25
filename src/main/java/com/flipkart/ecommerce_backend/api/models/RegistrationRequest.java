package com.flipkart.ecommerce_backend.api.models;

import jakarta.validation.constraints.*;

public record RegistrationRequest(
		@NotBlank(message = "User Name cannot be blank")
		@Size(min = 3, max = 20, message = "User Name must be between 3 and 20 characters")
		@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "User Name can only contain letters, numbers, and underscores")
		String user_name,

		@NotBlank(message = "Email cannot be blank")
		@Email(message = "Email should be valid")
		String email,

		@NotBlank(message = "Password cannot be blank")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
				message = "Password is weak,it must contain at least 8 characters, include uppercase, lowercase, numbers, and special characters")
		String password,

		@NotBlank(message = "First Name cannot be blank")
		@Size(max = 50, message = "First Name cannot exceed 50 characters")
		String first_name,

		@NotBlank(message = "Last Name cannot be blank")
		@Size(max = 50, message = "Last Name cannot exceed 50 characters")
		String last_name
) {}
