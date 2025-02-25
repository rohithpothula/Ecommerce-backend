package com.flipkart.ecommerce_backend.api.models;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest implements Serializable {

	@NonNull
	@NotBlank(message = "User Name cannot be blank")
	@Size(min = 3, max = 20, message = "User Name must be between 3 and 20 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "User Name can only contain letters, numbers, and underscores")
	private String user_name;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Email should be valid")
	private String email;

	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	private String password;

	@NotBlank(message = "First Name cannot be blank")
	@Size(max = 50, message = "First Name cannot exceed 50 characters")
	private String first_name;

	@NotBlank(message = "Last Name cannot be blank")
	@Size(max = 50, message = "Last Name cannot exceed 50 characters")
	private String last_name;
}
