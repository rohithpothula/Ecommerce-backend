package com.flipkart.ecommerce_backend.api.controllers.user;

import com.flipkart.ecommerce_backend.Exception.UnauthorizedAccessException;
import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;
import com.flipkart.ecommerce_backend.models.Address;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.services.impl.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/address")
public class userController {

  @Autowired private UserService userService;

  @Autowired private LocalUserRepository localUserRepository;

  @GetMapping("/{userId}")
  public ResponseEntity<GenericResponseBody> getAddress(
          @PathVariable UUID userId, @AuthenticationPrincipal LocalUser localUser) {
    GenericResponseBody genericResponseBody = new GenericResponseBody();
    GenericResponseBody response = new GenericResponseBody();

    if (localUser.getId().equals(userId)) {  // Use equals() instead of ==
      List<Address> addresses = userService.getAddresses(userId);
      response.setSuccess(true);
      response.setMessage("Addresses retrieved successfully");
      Map<String, Object> details = new HashMap<>();
      details.put("addresses", addresses);
      details.put("count", addresses.size());
      details.put("userId", userId);
      response.setDetails(details);
      response.setTimestamp(LocalDateTime.now().toString());
      response.setStatus("ADDRESS_RETRIEVAL_SUCCESSFUL");
      return ResponseEntity.ok(response);
    } else {
      response.setSuccess(false);
      response.setMessage(HttpStatus.FORBIDDEN.getReasonPhrase());
      response.setFailureReason("USER_IS_NOT_AUTHORIZED_TO_ACCESS");
      response.setStatus(HttpStatus.FORBIDDEN.getReasonPhrase());
      response.setTimestamp(LocalDateTime.now().toString());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
  }

  @PostMapping("/{userId}")
  public ResponseEntity<Address> saveAddress(
      @PathVariable UUID userId,
      @AuthenticationPrincipal LocalUser localUser,
      @RequestBody Address address) {
    GenericResponseBody genericResponseBody = new GenericResponseBody();
    log.debug("Saving address for user ID: {}", userId);

    validateUserAccess(localUser, userId);
    Address savedAddress = userService.addAddress(userId, address);

    log.info("Successfully saved address for user ID: {}", userId);
    return ResponseEntity.ok(savedAddress);
  }
  private void validateUserAccess(LocalUser authenticatedUser, UUID requestedUserId) {
    if (!authenticatedUser.getId().equals(requestedUserId)) {
      log.warn("Unauthorized access attempt by user {} for user ID {}", authenticatedUser.getId(), requestedUserId);
      throw new UnauthorizedAccessException(
              String.format("User %d is not authorized to access resources of user %d",
                      authenticatedUser.getId(), requestedUserId)
      );
    }
  }
}
