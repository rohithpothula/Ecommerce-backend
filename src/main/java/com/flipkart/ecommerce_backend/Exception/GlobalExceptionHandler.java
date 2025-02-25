package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error ->{
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			if ("NotBlank".equals(error.getCode())) {
				errors.put(fieldName, errorMessage); // @NotBlank takes precedence
			} else if (!errors.containsKey(fieldName)) { // Add only if no higher-priority error exists
				errors.put(fieldName, errorMessage != null ? errorMessage : "Validation failed");
			}		});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler
	public ResponseEntity<GenericResponseBody> userDoesNotExistsException(UserDoesNotExistsException ex){
		return new ResponseEntity<GenericResponseBody>(HttpStatus.NOT_FOUND);

	}

}
