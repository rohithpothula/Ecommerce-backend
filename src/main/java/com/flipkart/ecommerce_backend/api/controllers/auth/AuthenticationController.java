package com.flipkart.ecommerce_backend.api.controllers.auth;

import com.flipkart.ecommerce_backend.api.models.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.ecommerce_backend.Constants.ErrorConstants;
import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.InvalidEmailVerificationTokenException;
import com.flipkart.ecommerce_backend.Exception.InvalidUserCredentialsException;
import com.flipkart.ecommerce_backend.Exception.MailNotSentException;
import com.flipkart.ecommerce_backend.Exception.MailNotfoundException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserDoesNotExistsException;
import com.flipkart.ecommerce_backend.Exception.UserNotVerifiedException;
import com.flipkart.ecommerce_backend.Exception.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.Exception.VerificationTokenExpiredException;
import com.flipkart.ecommerce_backend.api.models.RegistrationResponse;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

	@Autowired
	private UserService userService;

	@PostMapping("/registeruser")
	public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) throws UserAlreadyExistsException, EmailAlreadyExistsException, MailNotSentException {
		try {
			LocalUser localuser = userService.registerUser(registrationRequest);
			RegistrationResponse responseBody = new RegistrationResponse(registrationRequest.email());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
		} catch (UserAlreadyExistsException e){
			RegistrationResponse registrationResponse = new RegistrationResponse(RegistrationResponse.RegistrationStatus.REGISTRATION_FAILED,"Username already exists.",registrationRequest.email());
			registrationResponse.setStatus(RegistrationResponse.RegistrationStatus.REGISTRATION_FAILED);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(registrationResponse);
		} catch (EmailAlreadyExistsException e) {
			RegistrationResponse registrationResponse = new RegistrationResponse(RegistrationResponse.RegistrationStatus.REGISTRATION_FAILED,"Email address already registered.",registrationRequest.email());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(registrationResponse);
		} catch (MailNotSentException e) {
			RegistrationResponse registrationResponse = new RegistrationResponse(RegistrationResponse.RegistrationStatus.REGISTRATION_FAILED,"Internal Server Error.",registrationRequest.email());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(registrationResponse);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponseBody> loginUser(@RequestBody LoginBody loginbody) throws MailNotSentException, UserNotVerifiedException, UserDoesNotExistsException, InvalidUserCredentialsException{
		AuthenticationResponseBody authenticationResponseBody = new AuthenticationResponseBody();
		String jwt=null;
		try {
			jwt = userService.loginUser(loginbody);
		}
		catch (UserNotVerifiedException ex){
			authenticationResponseBody.setIsSuccess(false);
			String reason = "USER_NOT_VERIFIED";
			if(ex.isEmailSent()) {
				reason = reason + "_EMAIL_RESENT";
			}
			authenticationResponseBody.setFailureReason(reason);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authenticationResponseBody);
		}
		catch (MailNotSentException e){
			authenticationResponseBody.setIsSuccess(false);
			authenticationResponseBody.setFailureReason(ErrorConstants.INVALID_PASSWORD);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authenticationResponseBody);
		}
		catch (InvalidUserCredentialsException e) {
			authenticationResponseBody.setIsSuccess(false);
			authenticationResponseBody.setFailureReason("INVALID_PASSWORD");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationResponseBody);
		}
		if(jwt==null) {
			authenticationResponseBody.setIsSuccess(false);
			authenticationResponseBody.setFailureReason("JWT_TOKEN_SENT_IS_NULL");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authenticationResponseBody);
		}
		else {
			authenticationResponseBody.setJwtToken(jwt);
			authenticationResponseBody.setIsSuccess(true);
			return new ResponseEntity<>(authenticationResponseBody,HttpStatus.OK);
		}
	}

	@GetMapping("/me")
	public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser authenticationPrinciple) {
		return authenticationPrinciple;
	}

	@PostMapping("/verify")
	public ResponseEntity<GenericResponseBody> verifyToken(@RequestParam String token) throws VerificationTokenExpiredException, InvalidEmailVerificationTokenException, UserVerificationTokenAlreadyVerifiedException, MailNotSentException{
		GenericResponseBody verificationTokenResponseBody = new GenericResponseBody();
		boolean verifyEmail=true;
		try {
			verifyEmail = userService.verifyToken(token);
			if(verifyEmail==true) {
				verificationTokenResponseBody.setIsSuccess(true);
				return ResponseEntity.status(HttpStatus.OK).body(verificationTokenResponseBody);
			}
		}
		catch(VerificationTokenExpiredException e) {
			verificationTokenResponseBody.setIsSuccess(false);
			verificationTokenResponseBody.setFailureReason("VERIFICATION TOKEN EXPIRED");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verificationTokenResponseBody);
		}
		catch (MailNotSentException e){
			verificationTokenResponseBody.setIsSuccess(false);
			verificationTokenResponseBody.setFailureReason("INTERNAL_SERVER_ERROR");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(verificationTokenResponseBody);
		}
		catch (InvalidEmailVerificationTokenException e) {
			verificationTokenResponseBody.setIsSuccess(false);
			verificationTokenResponseBody.setFailureReason("INVALID_VERIFICATION_TOKEN_ERROR");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verificationTokenResponseBody);
		}
		catch (UserVerificationTokenAlreadyVerifiedException e) {
			verificationTokenResponseBody.setIsSuccess(false);
			verificationTokenResponseBody.setFailureReason("USER_VERFICATION_TOKEN_ALREADY_VERIFIFED_ERROR");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verificationTokenResponseBody);
		}
		catch (Exception e) {
			verificationTokenResponseBody.setIsSuccess(false);
			verificationTokenResponseBody.setFailureReason(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(verificationTokenResponseBody);
		}
		return null;
	}

	@PostMapping("/reset")
	public ResponseEntity<GenericResponseBody> resetPassword(@RequestBody PasswordResetBody passwordResetBody) throws InvalidEmailVerificationTokenException, VerificationTokenExpiredException{
		GenericResponseBody genericResponseBody = new GenericResponseBody();
		try {
			userService.resetPassword(passwordResetBody.getToken(),passwordResetBody.getNewPassword());
			genericResponseBody.setIsSuccess(true);
			genericResponseBody.setFailureReason(null);
			return ResponseEntity.status(HttpStatus.OK).body(genericResponseBody);
		}
		catch(InvalidEmailVerificationTokenException e) {
			genericResponseBody.setFailureReason("Invalid token has been received");
			genericResponseBody.setIsSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(genericResponseBody);
		}
		catch(VerificationTokenExpiredException e) {
			genericResponseBody.setFailureReason("TOKEN_HAS_BEEN_EXPIRED");
			genericResponseBody.setIsSuccess(false);
			return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(genericResponseBody);
		}
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<GenericResponseBody> forgotPassword(@RequestParam String email) throws MailNotfoundException, MailNotSentException {
		GenericResponseBody forgotPasswordResponseBody = new GenericResponseBody();
		try {
			userService.forgotPassword(email);
			forgotPasswordResponseBody.setIsSuccess(true);
			forgotPasswordResponseBody.setFailureReason(null);
			return ResponseEntity.status(HttpStatus.OK).body(forgotPasswordResponseBody);
		}
		catch(MailNotfoundException e) {
			forgotPasswordResponseBody.setIsSuccess(false);
			forgotPasswordResponseBody.setFailureReason("INVALID_EMAIL_RECEIVED OR EMAIL IS NOT REGISTERED");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(forgotPasswordResponseBody);
		}
		catch(MailNotSentException e) {
			forgotPasswordResponseBody.setIsSuccess(false);
			forgotPasswordResponseBody.setFailureReason("PASSWORD_RESENT_MAIL_NOT_SENT");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(forgotPasswordResponseBody);
		}
		catch(Exception e) {
			forgotPasswordResponseBody.setIsSuccess(false);
			forgotPasswordResponseBody.setFailureReason("INTERNAL_SERVER_ERROR");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(forgotPasswordResponseBody);
		}
	}
}
