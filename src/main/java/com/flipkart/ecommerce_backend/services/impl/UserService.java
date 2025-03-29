package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.Exception.*;
import com.flipkart.ecommerce_backend.api.models.*;
import com.flipkart.ecommerce_backend.hander.ErrorCode;
import com.flipkart.ecommerce_backend.models.*;
import com.flipkart.ecommerce_backend.models.Repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserService {

  @Autowired private LocalUserRepository userRepository;
  @Autowired private EncryptionService encryptionService;
  @Autowired private JwtService jwtService;
  @Autowired private EmailService emailService;
  @Autowired private VerificationTokenRepository verificationTokenRepository;
  @Autowired private AddressRepository addressRepository;

  @Transactional
  public LocalUser registerUser(RegistrationRequest registrationRequest)
          throws UserAlreadyExistsException, EmailAlreadyExistsException, EmailSendException {
    log.debug("Starting user registration for username: {}", registrationRequest.user_name());

    validateNewUser(registrationRequest);
    LocalUser localUser = createLocalUser(registrationRequest);
    localUser = saveUser(localUser);
    sendVerificationEmail(localUser);

    log.info("Successfully registered user with ID: {}", localUser.getId());
    return localUser;
  }

  @Transactional(readOnly = true)
  public String loginUser(LoginBody loginBody) throws UserNotFoundException,
          UserNotVerifiedException, InvalidUserCredentialsException {
    log.debug("Attempting login for user: {}", loginBody.username());

    LocalUser user = findUserByUsername(loginBody.username());
    validateUserCredentials(user, loginBody.password());
    validateUserVerification(user);

    String jwt = jwtService.generateJWT(user);
    log.info("Successfully logged in user: {}", user.getUsername());
    return jwt;
  }
  @Transactional(readOnly = true)
  public List<Address> getAddresses(UUID userId) {
    log.debug("Fetching addresses for user ID: {}", userId);

    findUserById(userId);

    try {
      List<Address> addresses = addressRepository.findByLocalUser_Id(userId);
      log.debug("Found {} addresses for user ID: {}", addresses.size(), userId);
      return addresses;
    } catch (Exception e) {
      log.error("Error fetching addresses for user {}: {}", userId, e.getMessage());
      throw new DatabaseException(
              ErrorCode.DATABASE_ERROR,
              "Failed to fetch addresses",
              e.getMessage()
      );
    }
  }
  @Transactional
  public Address addAddress(UUID userId, Address address) {
    log.debug("Adding new address for user ID: {}", userId);

    LocalUser user = findUserById(userId);
    address.setLocalUser(user);

    try {
      Address savedAddress = addressRepository.save(address);
      log.info("Successfully added address ID: {} for user ID: {}",
              savedAddress.getId(), userId);
      return savedAddress;
    } catch (DataIntegrityViolationException e) {
      log.error("Failed to save address for user {}: {}", userId, e.getMessage());
      throw new DatabaseException(
              ErrorCode.DATABASE_CONSTRAINT_VIOLATION,
              "Failed to save address",
              e.getMessage()
      );
    }
  }

  @Transactional
  public LocalUser updateUser(UpdateUserDto updateRequest, UUID userId)
          throws UserNotFoundException {
    log.debug("Updating user with ID: {}", userId);

    LocalUser user = findUserById(userId);
    updateUserDetails(user, updateRequest);
    user = saveUser(user);

    log.info("Successfully updated user with ID: {}", userId);
    return user;
  }

  @Transactional(readOnly = true)
  public LocalUser getUserById(UUID userId) throws UserNotFoundException {
    return findUserById(userId);
  }

  @Transactional(readOnly = true)
  public List<LocalUser> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void deleteUser(UUID userId) {
    log.debug("Deleting user with ID: {}", userId);

    findUserById(userId); // Verify user exists
    userRepository.deleteById(userId);

    log.info("Successfully deleted user with ID: {}", userId);
  }

  @Transactional
  public boolean verifyToken(String token) throws VerificationTokenExpiredException,
          InvalidEmailVerificationTokenException, UserVerificationTokenAlreadyVerifiedException,
          EmailSendException {
    log.debug("Verifying token: {}", token);

    String email = jwtService.getEmailFromToken(token);
    LocalUser user = findUserByEmail(email);

    validateTokenVerification(user, token);
    completeUserVerification(user);

    log.info("Successfully verified email for user: {}", user.getEmail());
    return true;
  }

  @Transactional
  public void forgotPassword(String email) throws EmailNotFoundException, EmailSendException {
    log.debug("Processing forgot password request for email: {}", email);

    LocalUser user = findUserByEmail(email);
    String token = jwtService.generatePasswordResetJwt(user);
    emailService.sendPasswordRestVerificationMail(token, user);

    log.info("Successfully sent password reset email to: {}", email);
  }

  @Transactional
  public String resetPassword(String token, String newPassword)
          throws InvalidEmailVerificationTokenException, VerificationTokenExpiredException {
    log.debug("Processing password reset request");

    String email = jwtService.getPasswordResetEmailFromToken(token);
    LocalUser user = findUserByEmail(email);

    validatePasswordResetToken(token);
    updateUserPassword(user, newPassword);

    log.info("Successfully reset password for user: {}", user.getEmail());
    return email;
  }
  private void validatePasswordResetToken(String token) {
    boolean isTokenExpired = jwtService.isTokenExpired(token);
    if (isTokenExpired) {
      log.warn("Password reset token has expired: {}", token);
      throw new VerificationTokenExpiredException(token);
    }
  }

  private void updateUserPassword(LocalUser user, String newPassword) {
    try {
      String encryptedPassword = encryptionService.encryptPassword(newPassword);
      user.setPassword(encryptedPassword);
      userRepository.save(user);
      log.info("Successfully updated password for user: {}", user.getEmail());
    } catch (DataIntegrityViolationException e) {
      log.error("Failed to update user password: {}", e.getMessage());
      throw new DatabaseException("Failed to update password", e.getMessage());
    }
  }
  private void validateNewUser(RegistrationRequest request) {
    userRepository.findByUsernameIgnoreCase(request.user_name())
            .ifPresent(user -> {
              throw new UserAlreadyExistsException(request.user_name());
            });

    userRepository.findByEmailIgnoreCase(request.email())
            .ifPresent(user -> {
              throw new EmailAlreadyExistsException(request.email());
            });
  }

  private LocalUser createLocalUser(RegistrationRequest request) {
    LocalUser user = new LocalUser();
    user.setUsername(request.user_name());
    user.setPassword(encryptionService.encryptPassword(request.password()));
    user.setEmail(request.email());
    user.setFirstName(request.first_name());
    user.setLastName(request.last_name());
    return user;
  }

  private LocalUser saveUser(LocalUser user) {
    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      log.error("Database error while saving user: {}", e.getMessage());
      throw new DatabaseException("Failed to save user", e.getMessage());
    }
  }

  private void sendVerificationEmail(LocalUser user) {
    VerificationToken token = createVerificationToken(user);
    try {
      emailService.sendVerificationMail(token);
    } catch (EmailSendException e) {
      log.error("Failed to send verification email to user: {}", user.getEmail());
      throw e;
    }
  }

  private VerificationToken createVerificationToken(LocalUser user) {
    VerificationToken token = new VerificationToken();
    token.setToken(jwtService.generateEmailJwt(user));
    token.setLocalUser(user);
    token.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));

    try {
      token = verificationTokenRepository.save(token);
      user.getVerificationTokens().add(token);
      return token;
    } catch (DataIntegrityViolationException e) {
      log.error("Database error while saving verification token: {}", e.getMessage());
      throw new DatabaseException("Failed to create verification token", e.getMessage());
    }
  }

  private LocalUser findUserById(UUID userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));
  }

  private LocalUser findUserByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UserNotFoundException(email));
  }

  public LocalUser findUserByUsername(String username) {
    return userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new UserNotFoundException(username));
  }
  private void validateTokenVerification(LocalUser user, String token) {
    if (user.isEmailVerified()) {
      log.warn("User already verified: {}", user.getEmail());
      throw new UserVerificationTokenAlreadyVerifiedException();
    }

    if (jwtService.isTokenExpired(token)) {
      log.info("Token expired, generating new one for user: {}", user.getEmail());
      VerificationToken newToken = createVerificationToken(user);
      emailService.sendVerificationMail(newToken);
      throw new VerificationTokenExpiredException(token);
    }
  }

  private void completeUserVerification(LocalUser user) {
    try {
      user.setEmailVerified(true);
      userRepository.save(user);
      log.info("Successfully verified user: {}", user.getEmail());
    } catch (DataIntegrityViolationException e) {
      log.error("Failed to update user verification status: {}", e.getMessage());
      throw new DatabaseException("Failed to verify user", e.getMessage());
    }
  }

  private void validateUserCredentials(LocalUser user, String password) {
    if (!encryptionService.verifyPassword(password, user.getPassword())) {
      log.warn("Invalid password attempt for user: {}", user.getUsername());
      throw new InvalidUserCredentialsException("Invalid username or password");
    }
  }

  private void updateUserDetails(LocalUser user, UpdateUserDto updateRequest) {
    user.setUsername(updateRequest.getUser_name());
    user.setFirstName(updateRequest.getFirst_name());
    user.setEmail(updateRequest.getEmail());
    user.setLastName(updateRequest.getLast_name());
  }
  private void validateUserVerification(LocalUser user) {
    if (!user.isEmailVerified()) {
      log.debug("User {} not verified, checking verification status", user.getUsername());
      handleUnverifiedUser(user);
      throw new UserNotVerifiedException(user.getUsername());
    }
  }

  private void handleUnverifiedUser(LocalUser user) {
    List<VerificationToken> tokens = user.getVerificationTokens();
    boolean shouldResendToken = shouldResendVerificationToken(tokens);

    if (shouldResendToken) {
      log.info("Resending verification email for user: {}", user.getUsername());
      sendVerificationEmail(user);
    }
  }

  private boolean shouldResendVerificationToken(List<VerificationToken> tokens) {
    if (tokens.isEmpty()) {
      return true;
    }

    VerificationToken latestToken = tokens.get(0); // Assuming tokens are ordered desc by ID
    return isTokenExpired(latestToken);
  }

  private boolean isTokenExpired(VerificationToken token) {
    Timestamp expiryTime = new Timestamp(System.currentTimeMillis() - (1000 * 60 * 60)); // 1 hour
    return token.getCreatedTimestamp().before(expiryTime);
  }
}
