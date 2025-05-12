package com.flipkart.ecommerce_backend.controllers.auth;

import com.flipkart.ecommerce_backend.dtos.GenericResponseBodyDto;
import com.flipkart.ecommerce_backend.services.EmailVerificationService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

  private final EmailVerificationService emailVerificationService;

  /**
   * Endpoint to verify a user's email address using the provided token. This is typically hit when
   * the user clicks the link in their verification email. Publicly accessible.
   *
   * @param token The verification token string from the URL parameter.
   * @return ResponseEntity indicating success or failure (via exception handling).
   */
  @GetMapping("/email") // Use GET as clicking a link is often idempotent
  public ResponseEntity<GenericResponseBodyDto> verifyEmail(@RequestParam String token) {
    log.info("Received email verification request with token");
    try {
      emailVerificationService.verifyEmailWithToken(token);
      GenericResponseBodyDto response = new GenericResponseBodyDto();
      response.setSuccess(true);
      response.setMessage("Email successfully verified. You can now log in.");
      response.setStatus("EMAIL_VERIFICATION_SUCCESS");
      response.setDetails(Map.of("token", token));
      response.setTimestamp(LocalDateTime.now().toString());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Email verification failed: {}", e.getMessage()); // Log concise error
      throw e;
    }
  }

  // Optional: Add an endpoint to RESEND the verification email if needed
}
