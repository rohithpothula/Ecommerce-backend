package com.flipkart.ecommerce_backend.controllers.user;

import com.flipkart.ecommerce_backend.dtos.*;
import com.flipkart.ecommerce_backend.models.ERole;
import com.flipkart.ecommerce_backend.services.UserManagementService;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

  private final UserManagementService userManagementService;


  @PostMapping("/admin/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> createAdminUser(
          @Valid @RequestBody RegistrationRequest registrationRequest) throws RoleNotFoundException {
    log.warn("[Admin Action] Request received to create potential admin user: {}", registrationRequest.user_name());

    UserDto createdUser = userManagementService.registerUser(registrationRequest);



    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
  /**
   * [ADMIN] Retrieves a list of all users.
   * Requires ADMIN role. Add pagination parameters for production.
   *
   * @return ResponseEntity containing a list of UserDTOs.
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GenericResponseBodyDto> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
    log.info("[Admin Action] Request received to get all users");
    Page<UserDto> users = userManagementService.getAllUsers(pageable);
    GenericResponseBodyDto response = new GenericResponseBodyDto();
    response.setSuccess(true);
    response.setMessage("Users retrieved successfully");
    response.setDetails(Map.of("users", users));
    response.setTimestamp(LocalDateTime.now().toString());
    response.setStatus("USERS_RETRIEVAL_SUCCESSFUL");
    return ResponseEntity.ok(response);
  }

  /**
   * [ADMIN] Updates an existing user's details.
   * Requires ADMIN role.
   */
  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateUser(
          @PathVariable UUID userId,
          @Valid @RequestBody UserUpdateRequest updateRequest) {
    log.info("[Admin Action] Request received to update user ID: {}", userId);
    UserDto updatedUser = userManagementService.updateUser(userId, updateRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @GetMapping("/role/{roleName}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UserDto>> getUsersByRole(
          @PathVariable ERole roleName,
          @PageableDefault(size = 20) Pageable pageable) {
    log.info("[Admin Action] Request received to get users by role '{}' (page={}, size={}, sort={})",
            roleName, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    Page<UserDto> usersPage = userManagementService.getUsersByRole(roleName, pageable);
    return ResponseEntity.ok(usersPage);
  }

  /**
   * [ADMIN] Deletes a user by their unique ID.
   * Requires ADMIN role.
   */
  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    log.warn("[Admin Action] Request received to DELETE user ID: {}", userId);
    userManagementService.deleteUser(userId);
    log.warn("[Admin Action] User deleted successfully: {}", userId);
    return ResponseEntity.noContent().build();
  }

  /**
   * [ADMIN] Directly changes a user's password. Use with extreme caution.
   * Requires ADMIN role. Expects raw password in the request body.
   */
  @PostMapping("/{userId}/password/change-admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> changePasswordAdmin(
          @PathVariable UUID userId,
          @Valid @RequestBody AdminPasswordChangeRequest request) {
    log.warn("[Admin Action] Request received to directly change password for user ID: {}", userId);
    userManagementService.changePasswordAdmin(userId, request.getNewPassword());
    return ResponseEntity.ok("Admin successfully changed password for user " + userId);
  }

}
