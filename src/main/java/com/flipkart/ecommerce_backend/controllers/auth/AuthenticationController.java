package com.flipkart.ecommerce_backend.controllers.auth;

import com.flipkart.ecommerce_backend.dtos.*;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.AuthenticationService;
import com.flipkart.ecommerce_backend.services.EmailService;
import com.flipkart.ecommerce_backend.services.EmailVerificationService;
import com.flipkart.ecommerce_backend.services.UserManagementService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  /** /register - POST /login - POST /logout - POST /refresh - POST /me - GET */
  private final AuthenticationService authenticationService;

  private final UserManagementService userManagementService;
  private final EmailVerificationService emailVerificationService;
  private final EmailService emailService;

  @PostMapping("/register")
  public ResponseEntity<UserDto> registerUser(
      @Valid @RequestBody RegistrationRequest registrationRequest) throws RoleNotFoundException {
    UserDto savedUserDTO = userManagementService.registerUser(registrationRequest);

    try {
      LocalUser registeredUser =
          userManagementService.findEntityByUsernameOrEmail(savedUserDTO.username());
      String verificationToken =
          emailVerificationService.createAndPersistVerificationToken(registeredUser);
      emailService.sendVerificationMail(registeredUser, verificationToken);
      log.info("Verification email initiated for user: {}", savedUserDTO.username());
    } catch (Exception e) {
      log.error(
          "Error during post-registration email verification steps for {}: {}",
          savedUserDTO.username(),
          e.getMessage(),
          e);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(savedUserDTO);
  }

  @PostMapping("/login")
  public ResponseEntity<GenericResponseBodyDto> loginUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    log.debug("Processing login request for username: {}", loginRequest.username());

    LoginResponse loginResponse = authenticationService.login(loginRequest);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof LocalUser)) {
      log.error(
          "Authentication failed or principal is not LocalUser after successful login service call for {}",
          loginRequest.username());
      throw new IllegalStateException(
          "Authentication principal is not available or not of expected type after login.");
    }
    LocalUser user = (LocalUser) authentication.getPrincipal();

    GenericResponseBodyDto response = new GenericResponseBodyDto();
    response.setSuccess(true);
    response.setMessage("User logged in successfully");
    response.setStatus("LOGIN_SUCCESSFUL");
    response.setTimestamp(LocalDateTime.now().toString());
    Map<String, Object> details = new HashMap<>();
    details.put("username", user.getUsername());
    details.put("email", user.getEmail());
    details.put("roles", user.getAuthorities());
    details.put("lastLogin", LocalDateTime.now().toString());
    details.put("accessToken", loginResponse.getAccessToken());
    response.setDetails(details);

    log.info("Successfully logged in user: {}", loginRequest.username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<GenericResponseBodyDto> logoutUser(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    log.debug("Processing logout request");

    authenticationService.logout(refreshTokenRequest);

    GenericResponseBodyDto response = new GenericResponseBodyDto();
    response.setSuccess(true);
    response.setMessage("User logged out successfully");
    response.setStatus("LOGOUT_SUCCESSFUL");
    response.setTimestamp(LocalDateTime.now().toString());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(
      @RequestBody RefreshTokenRequest refreshTokenRequest) {
    log.debug("Processing token refresh request");

    LoginResponse response = authenticationService.refreshToken(refreshTokenRequest);

    log.info("Successfully refreshed token");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getLoggedInUserProfile(
      @AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      log.warn("Attempt to access /me endpoint without valid authentication.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    log.debug("Fetching profile for authenticated user: {}", userDetails.getUsername());
    UserDto userDTO = userManagementService.findUserDTOByUsernameOrEmail(userDetails.getUsername());
    return ResponseEntity.ok(userDTO);
  }
}
