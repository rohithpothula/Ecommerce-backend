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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.InvalidEmailVerificationTokenException;
import com.flipkart.ecommerce_backend.Exception.InvalidUserCredentialsException;
import com.flipkart.ecommerce_backend.Exception.MailNotSentException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserDoesNotExistsException;
import com.flipkart.ecommerce_backend.Exception.UserNotVerifiedException;
import com.flipkart.ecommerce_backend.Exception.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.Exception.VerificationTokenExpiredException;
import com.flipkart.ecommerce_backend.api.models.AuthenticationResponseBody;
import com.flipkart.ecommerce_backend.api.models.LoginBody;
import com.flipkart.ecommerce_backend.api.models.RegistrationBody;
import com.flipkart.ecommerce_backend.api.models.VerificationTokenResponseBody;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.VerificationToken;
import com.flipkart.ecommerce_backend.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/registeruser")
	public ResponseEntity<LocalUser> registerUser(@RequestBody RegistrationBody regestrationBody) throws UserAlreadyExistsException, EmailAlreadyExistsException, MailNotSentException {
		try {
				LocalUser localuser = userService.registerUser(regestrationBody);
				return ResponseEntity.ok().build();

		}
		catch (UserAlreadyExistsException e){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponseBody> loginUser(@RequestBody LoginBody loginbody) throws MailNotSentException, UserNotVerifiedException, UserDoesNotExistsException, InvalidUserCredentialsException{
		AuthenticationResponseBody authenticationResponseBody = new AuthenticationResponseBody();
		String jwt=null;
		try {
			jwt = userService.loginUser(loginbody);
		}
		catch (UserNotVerifiedException e){
			authenticationResponseBody.setSuccess(false);
			authenticationResponseBody.setFailureReason("USER_NOT_VERIFIED");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authenticationResponseBody);
		}
		catch (MailNotSentException e){
			authenticationResponseBody.setSuccess(false);
			authenticationResponseBody.setFailureReason("INTERNAL_SERVER_ERROR");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authenticationResponseBody);
		}
		if(jwt==null) {
			authenticationResponseBody.setSuccess(false);
			authenticationResponseBody.setFailureReason("JWT_TOKEN_SENT_IS_NULL");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authenticationResponseBody);
		}
		else {
			authenticationResponseBody.setJwtToken(jwt);
			authenticationResponseBody.setSuccess(true);
			return new ResponseEntity<>(authenticationResponseBody,HttpStatus.OK); 
		}
	}
	
	@GetMapping("/me")
	public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser authenticationPrinciple) {
		return authenticationPrinciple;
	}
	
	public ResponseEntity<VerificationToken> verifyToken(@RequestParam String token) throws VerificationTokenExpiredException, InvalidEmailVerificationTokenException, UserVerificationTokenAlreadyVerifiedException, MailNotSentException{
		VerificationTokenResponseBody verificationTokenResponseBody = new VerificationTokenResponseBody();
		boolean verifyEmail=true;
		try {
			verifyEmail = userService.verifyToken(token);
		}
		catch(VerificationTokenExpiredException e) {
			verificationTokenResponseBody.setFailureReason("VERIFICATION TOKEN EXPIRED");
			
		}
		if(verifyEmail==true) {
			return (ResponseEntity<VerificationToken>) ResponseEntity.status(HttpStatus.OK);
		}
		else {
			return (ResponseEntity<VerificationToken>) ResponseEntity.status(HttpStatus.BAD_REQUEST);
		}
	}
}
