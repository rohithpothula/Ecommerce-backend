package com.flipkart.ecommerce_backend.api.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.api.models.AuthenticationResponseBody;
import com.flipkart.ecommerce_backend.api.models.LoginBody;
import com.flipkart.ecommerce_backend.api.models.RegistrationBody;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/registeruser")
	public ResponseEntity<LocalUser> registerUser(@RequestBody RegistrationBody regestrationBody) throws UserAlreadyExistsException, EmailAlreadyExistsException {
		try {
				LocalUser localuser = userService.registerUser(regestrationBody);
				return ResponseEntity.ok().build();

		}
		catch (UserAlreadyExistsException e){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponseBody> loginUser(@RequestBody LoginBody loginbody) throws Exception{
		String jwt = userService.loginUser(loginbody);
		AuthenticationResponseBody authenticationResponseBody = new AuthenticationResponseBody();
		if(jwt==null) {
			return new ResponseEntity<>(authenticationResponseBody,HttpStatus.BAD_REQUEST);
		}
		else {
			authenticationResponseBody.setJwtToken(jwt);
			return new ResponseEntity<>(authenticationResponseBody,HttpStatus.OK); 
		}
	}
	
	@GetMapping("/me")
	public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser authenticationPrinciple) {
//		AuthenticationPrincipal authenticationPrinciple =  (AuthenticationPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		LocalUser localuser = (LocalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return authenticationPrinciple;
	}
}
