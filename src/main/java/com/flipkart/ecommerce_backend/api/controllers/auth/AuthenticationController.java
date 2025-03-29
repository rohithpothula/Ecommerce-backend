package com.flipkart.ecommerce_backend.api.controllers.auth;

import com.flipkart.ecommerce_backend.Exception.*;
import com.flipkart.ecommerce_backend.api.models.*;
import com.flipkart.ecommerce_backend.api.models.RegistrationResponse;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.impl.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

  @Autowired private UserService userService;

  @PostMapping("/registeruser")
  public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
    log.debug("Processing registration request for email: {}", registrationRequest.email());

    LocalUser localUser = userService.registerUser(registrationRequest);
    RegistrationResponse response = new RegistrationResponse(registrationRequest.email());

    log.info("Successfully registered user with email: {}", registrationRequest.email());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    log.debug("Processing login request for username: {}", loginBody.username());

    String jwt = userService.loginUser(loginBody);
    LocalUser user = userService.findUserByUsername(loginBody.username());
    LoginResponse response = LoginResponse.of(jwt, user);

    log.info("Successfully logged in user: {}", loginBody.username());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    if (user == null) {
      throw new UnauthorizedAccessException("User not authenticated");
    }
    return user;
  }

  @PostMapping("/verify")
  public ResponseEntity<SuccessResponse> verifyToken(@RequestParam String token) {
    log.debug("Processing token verification request");

    boolean verified = userService.verifyToken(token);

    if (verified) {
      SuccessResponse response = SuccessResponse.of("Email successfully verified");
      log.info("Successfully verified token");
      return ResponseEntity.ok(response);
    } else {
      throw new TokenVerificationException("Token verification failed unexpectedly");
    }
  }

  @PostMapping("/reset")
  public ResponseEntity<GenericResponseBody> resetPassword(@Valid @RequestBody PasswordResetBody passwordResetBody) {
    log.debug("Processing password reset request");

    // Call service to reset password and get user email
    String userEmail = userService.resetPassword(passwordResetBody.getToken(), passwordResetBody.getNewPassword());

    GenericResponseBody response = new GenericResponseBody();
    response.setSuccess(true);
    response.setMessage("Password has been successfully reset");
    response.setStatus("PASSWORD_RESET_SUCCESSFUL");
    response.setTimestamp(LocalDateTime.now().toString());

    Map<String, Object> details = new HashMap<>();
    details.put("email", userEmail);
    details.put("lastUpdated", LocalDateTime.now().toString());
    details.put("nextStep", "You can now login with your new password");
    response.setDetails(details);

    log.info("Successfully reset password for user: {}", userEmail);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/forgotpassword")
  public ResponseEntity<GenericResponseBody> forgotPassword(@RequestParam String email) {
    log.debug("Processing forgot password request for email: {}", email);

    userService.forgotPassword(email);

    GenericResponseBody response = new GenericResponseBody();
    response.setSuccess(true);
    response.setMessage("Password reset link has been sent to your email");
    response.setStatus("PENDING_EMAIL_VERIFICATION");
    response.setTimestamp(LocalDateTime.now().toString());

    Map<String, Object> details = new HashMap<>();
    details.put("email", email);
    details.put("expirationTime", "30 minutes");
    details.put("nextSteps", "Please check your email and follow the instructions to reset your password");
    response.setDetails(details);

    log.info("Successfully processed forgot password request for email: {}", email);
    return ResponseEntity.ok(response);
  }

}
