package com.flipkart.ecommerce_backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler
	public ResponseEntity<GenericResponseBody> userDoesNotExistsException(UserDoesNotExistsException ex){
		return new ResponseEntity<GenericResponseBody>(HttpStatus.NOT_FOUND);
		
	}

}
