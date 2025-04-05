package com.flipkart.ecommerce_backend.controllers.auth;

import com.flipkart.ecommerce_backend.dtos.ChangePasswordRequest;
import com.flipkart.ecommerce_backend.dtos.GenericResponseBodyDto;
import com.flipkart.ecommerce_backend.dtos.ResetPasswordRequest;
import com.flipkart.ecommerce_backend.security.service.PasswordManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/password")
public class PasswordController {
    /**
     * /forgot - POST
     * /reset - POST
     * /change - POST
     */
    @Autowired
    private PasswordManagementService passwordManagementService;


    @PostMapping("/forgot")
    public ResponseEntity<GenericResponseBodyDto> forgotPassword(@RequestParam @Email String email) {
        log.info("Received forgot password request for email: {}", email);

        try {
            passwordManagementService.initiatePasswordReset(email);
            // IMPORTANT: Do NOT confirm if the email exists to prevent user enumeration attacks.
            GenericResponseBodyDto response = new GenericResponseBodyDto();
            response.setMessage("If an account exists for this email, a password reset link has been sent.");
            response.setStatus("PASSWORD_RESET_LINK_SENT");
            response.setTimestamp(LocalDateTime.now().toString());
            response.setDetails(Map.of(
                    "email", email,
                    "nextStep", "Check your email for the password reset link"
            ));
            log.info("Password reset link sent to email: {}", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initiating password reset for email {}: {}", email, e.getMessage(), e);
            GenericResponseBodyDto response = new GenericResponseBodyDto();
            response.setMessage("Error initiating password reset. Please try again later.");
            response.setStatus("PASSWORD_RESET_ERROR");
            response.setTimestamp(LocalDateTime.now().toString());
            response.setDetails(Map.of(
                    "email", email,
                    "error", e.getMessage()
            ));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<GenericResponseBodyDto> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        log.info("Received request to reset password with token: {}", resetPasswordRequest.token()); // Avoid logging the password
        passwordManagementService.resetPassword(resetPasswordRequest);
        GenericResponseBodyDto response = new GenericResponseBodyDto();
        response.setMessage("Password has been reset successfully.");
        response.setStatus("PASSWORD_RESET_SUCCESSFUL");
        response.setTimestamp(LocalDateTime.now().toString());
        response.setDetails(Map.of(
                "token", resetPasswordRequest.token(),
                "nextStep", "You can now login with your new password"
        ));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change")
    public ResponseEntity<GenericResponseBodyDto> changePassword(
            @AuthenticationPrincipal UserDetails userDetails, // Get authenticated user
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (userDetails == null) {
            log.warn("User is not authenticated");
            GenericResponseBodyDto response = new GenericResponseBodyDto();
            response.setMessage("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        log.info("Received request to change password for user: {}", userDetails.getUsername());
        passwordManagementService.changePassword(userDetails.getUsername(), changePasswordRequest);
        GenericResponseBodyDto response = new GenericResponseBodyDto();
        response.setMessage("Password changed successfully");
        response.setStatus("PASSWORD_CHANGE_SUCCESSFUL");
        response.setTimestamp(LocalDateTime.now().toString());
        response.setDetails(Map.of(
                "userId", userDetails.getUsername(),
                "lastUpdated", LocalDateTime.now().toString(),
                "nextStep", "You can now login with your new password"
        ));
        log.info("Successfully changed password for user: {}", userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
