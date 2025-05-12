package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.dtos.RegistrationRequest;
import com.flipkart.ecommerce_backend.dtos.UserDto;
import com.flipkart.ecommerce_backend.dtos.UserUpdateRequest;
import com.flipkart.ecommerce_backend.exception.DatabaseException;
import com.flipkart.ecommerce_backend.exception.role.RoleNotFoundException;
import com.flipkart.ecommerce_backend.exception.user.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.exception.user.UserNotFoundException;
import com.flipkart.ecommerce_backend.models.ERole;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Role;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.repository.RoleRepository;
import com.flipkart.ecommerce_backend.security.store.TokenStore;
import com.flipkart.ecommerce_backend.services.UserManagementService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
  @Autowired private LocalUserRepository userRepository;
  @Autowired private RoleRepository roleRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private TokenStore tokenStore;

  @Override
  @Transactional
  public UserDto registerUser(RegistrationRequest registrationRequest) {
    log.debug("Starting user registration for username: {}", registrationRequest.user_name());
    userRepository
        .findByUsernameIgnoreCase(registrationRequest.user_name())
        .ifPresent(
            u -> {
              throw new UserAlreadyExistsException(
                  "Username '" + registrationRequest.user_name() + "' already exists.");
            });
    userRepository
        .findByEmailIgnoreCase(registrationRequest.email())
        .ifPresent(
            u -> {
              throw new UserAlreadyExistsException(
                  "Email '" + registrationRequest.email() + "' already exists.");
            });

    LocalUser newUser = new LocalUser();
    newUser.setUsername(registrationRequest.user_name());
    newUser.setEmail(registrationRequest.email());
    newUser.setFirstName(registrationRequest.first_name());
    newUser.setLastName(registrationRequest.last_name());
    newUser.setPassword(passwordEncoder.encode(registrationRequest.password()));
    newUser.setEnabled(false);
    newUser.setEmailVerified(false);
    newUser.setAccountNonExpired(true);
    newUser.setAccountNonLocked(true);
    newUser.setCredentialsNonExpired(true);
    Set<Role> roles = new HashSet<>();
    Role userRole =
        roleRepository
            .findByName(ERole.ROLE_USER) // Use enum name() for lookup if repo expects String
            .orElseThrow(
                () -> {
                  log.error("Configuration error: Default role ROLE_USER not found in database.");
                  // Consider a more specific configuration exception if appropriate
                  return new RoleNotFoundException("Default role ROLE_USER not found.");
                });
    roles.add(userRole);
    newUser.setRoles(roles);

    // Save
    LocalUser savedUser = saveUserInternal(newUser);
    log.info(
        "User entity registered successfully (pending verification) with ID: {}",
        savedUser.getId());
    return mapToUserDTO(newUser); // Return DTO
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findUserDTOByUsernameOrEmail(String usernameOrEmail) {
    log.debug("Finding user DTO by username or email: {}", usernameOrEmail);
    LocalUser user =
        userRepository
            .findByUsernameIgnoreCase(usernameOrEmail)
            .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail))
            .orElseThrow(
                () ->
                    new UserNotFoundException(
                        "User not found with username or email: " + usernameOrEmail));
    return mapToUserDTO(user);
  }

  @Override
  @Transactional(readOnly = true)
  public LocalUser findEntityByUsernameOrEmail(String usernameOrEmail) {
    log.debug("Finding user entity by username or email: {}", usernameOrEmail);
    return userRepository
        .findByUsernameIgnoreCase(usernameOrEmail)
        .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail))
        .orElseThrow(
            () ->
                new UserNotFoundException(
                    "User not found with username or email: " + usernameOrEmail));
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto getUserById(UUID userId) {
    log.debug("Fetching user by ID: {}", userId);
    LocalUser user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

    return mapToUserDTO(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> getAllUsers(Pageable pageable) {
    log.info(
        "Fetching all users - page: {}, size: {}",
        pageable.getPageNumber(),
        pageable.getPageSize());
    Page<LocalUser> userPage = userRepository.findAll(pageable);
    return userPage.map(this::mapToUserDTO);
  }

  @Override
  @Transactional
  public UserDto updateUser(UUID userId, UserUpdateRequest request) {
    log.info("Updating user with ID: {}", userId);
    LocalUser user = findUserByIdInternal(userId);

    Optional.ofNullable(request.firstName()).ifPresent(user::setFirstName);
    Optional.ofNullable(request.lastName()).ifPresent(user::setLastName);
    Optional.ofNullable(request.enabled()).ifPresent(user::setEnabled);

    // Handle email change
    if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())) {
      userRepository
          .findByEmailIgnoreCase(request.email())
          .ifPresent(
              existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                  throw new UserAlreadyExistsException(
                      "Email '" + request.email() + "' is already in use by another user.");
                }
              });
      log.debug("Updating email for user ID {}: requires re-verification", userId);
      user.setEmail(request.email());
      user.setEmailVerified(false); // Force re-verification
    }

    // Handle role updates
    if (request.roles() != null) {
      Set<Role> newRoles =
          request.roles().stream()
              .map(
                  roleName ->
                      roleRepository
                          .findByName(roleName)
                          .orElseThrow(
                              () -> new RoleNotFoundException("Role not found: " + roleName)))
              .collect(Collectors.toSet());
      log.debug("Updating roles for user ID {}: new roles = {}", userId, request.roles());
      user.setRoles(newRoles);
    }

    LocalUser updatedUser = saveUserInternal(user);
    log.info("User updated successfully for ID: {}", userId);
    return mapToUserDTO(updatedUser);
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    log.warn("Attempting to delete user with ID: {}", userId);
    LocalUser user = findUserByIdInternal(userId);
    tokenStore.deleteUserTokens(user);
    userRepository.delete(user);
    log.warn("User deleted successfully with ID: {}", userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> getUsersByRole(ERole roleName, Pageable pageable) {
    log.info(
        "Fetching users by role '{}' - page: {}, size: {}",
        roleName,
        pageable.getPageNumber(),
        pageable.getPageSize());
    Role role =
        roleRepository
            .findByName(roleName)
            .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
    Page<LocalUser> userPage = userRepository.findByRolesContains(role, pageable);
    return userPage.map(this::mapToUserDTO);
  }

  @Override
  @Transactional
  public void changePasswordAdmin(UUID userId, String newPassword) {
    log.warn("[ADMIN ACTION] Changing password for user ID: {}", userId);
    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("New password cannot be blank.");
    }
    LocalUser user = findUserByIdInternal(userId);
    user.setPassword(passwordEncoder.encode(newPassword));
    saveUserInternal(user);
    tokenStore.deleteUserTokens(user);
    log.warn("[ADMIN ACTION] Password changed and tokens invalidated for user ID: {}", userId);
  }

  @Override
  @Transactional
  public void resetPasswordAdmin(String email, String newPassword) {
    log.warn("[ADMIN ACTION] Resetting password for email: {}", email);
    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("New password cannot be blank.");
    }
    LocalUser user = findUserByEmailInternal(email);
    user.setPassword(passwordEncoder.encode(newPassword));
    saveUserInternal(user);
    tokenStore.deleteUserTokens(user);
    log.warn("[ADMIN ACTION] Password reset and tokens invalidated for email: {}", email);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    log.debug("Attempting to load user by username or email: {}", usernameOrEmail);
    LocalUser user =
        userRepository
            .findByUsernameIgnoreCase(usernameOrEmail)
            .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail))
            .orElseThrow(
                () -> {
                  log.warn("User not found with username or email: {}", usernameOrEmail);
                  return new UsernameNotFoundException(
                      "User not found with username or email: " + usernameOrEmail);
                });
    log.debug("User found: {}", user.getUsername());
    return user;
  }

  private LocalUser saveUserInternal(LocalUser user) {
    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      log.error("Database error while saving user {}: {}", user.getUsername(), e.getMessage());
      throw new DatabaseException("Failed to save user data.", e.getMessage());
    }
  }

  private LocalUser findUserByIdInternal(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
  }

  private LocalUser findUserByEmailInternal(String email) {
    return userRepository
        .findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
  }

  private UserDto mapToUserDTO(LocalUser user) {
    // Implement mapping (same as before)
    Set<String> roleNames =
        user.getRoles() == null
            ? new HashSet<>()
            : user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        roleNames,
        user.isEnabled(), // Include status flags if needed in DTO
        user.isEmailVerified());
  }
}
