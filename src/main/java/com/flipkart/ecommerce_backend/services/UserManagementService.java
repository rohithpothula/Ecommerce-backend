package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.exception.user.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.exception.user.UserNotFoundException;
import com.flipkart.ecommerce_backend.dtos.RegistrationRequest;
import com.flipkart.ecommerce_backend.dtos.UserUpdateRequest;
import com.flipkart.ecommerce_backend.models.ERole;
import com.flipkart.ecommerce_backend.models.LocalUser;
import jakarta.validation.Valid;
import com.flipkart.ecommerce_backend.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import javax.management.relation.RoleNotFoundException;
import java.util.UUID;

@Validated
public interface UserManagementService extends UserDetailsService {
    /**
     * Registers a new user based on the provided registration details.
     * Checks for existing username and email before creation.
     *
     * @param registrationRequest DTO containing user registration data. Must be valid.
     * @return A UserDTO representing the newly created user.
     * @throws UserAlreadyExistsException if the username or email is already taken.
     */
    UserDto registerUser(@Valid RegistrationRequest registrationRequest) throws RoleNotFoundException;

    /**
     * Finds a user by their username or email and returns their details as a DTO.
     * Excludes sensitive information like passwords.
     *
     * @param usernameOrEmail The username or email of the user to find.
     * @return A UserDTO representing the found user.
     * @throws UserNotFoundException if no user is found with the given username or email.
     */
    UserDto findUserDTOByUsernameOrEmail(String usernameOrEmail);

    /**
     * Finds a user by their username or email and returns the LocalUser entity.
     * This may include sensitive information like passwords.
     *
     * @param usernameOrEmail The username or email of the user to find.
     * @return A LocalUser entity representing the found user.
     * @throws UserNotFoundException if no user is found with the given username or email.
     */
    LocalUser findEntityByUsernameOrEmail(String usernameOrEmail);
    /**
     * Finds a user by their unique ID.
     * @param userId The ID of the user to find.
     * @return UserDTO representing the found user.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    UserDto getUserById(UUID userId);

    /**
     * Updates an existing user's details. (Potentially Admin Only)
     * Email/username changes should be handled carefully regarding uniqueness.
     * Role changes require appropriate checks.
     * @param userId The ID of the user to update.
     * @param request DTO containing the fields to update.
     * @return UserDTO representing the updated user.
     * @throws UserNotFoundException if the user ID is not found.
     * @throws UserAlreadyExistsException if trying to update email/username to one that already exists.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    UserDto updateUser(UUID userId, @Valid UserUpdateRequest request);

    /**
     * Deletes a user by their ID. (Potentially Admin Only)
     * Consider implications for related data (orders, tokens, etc.).
     * @param userId The ID of the user to delete.
     * @throws UserNotFoundException if the user ID is not found.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    void deleteUser(UUID userId);

    /**
     * Retrieves a list of all users. (Potentially Admin Only)
     * Consider pagination for large datasets in a real application.
     * @return A list of UserDTOs for all users.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Retrieves users assigned a specific role. (Potentially Admin Only)
     * Requires Role entity and repository setup.
     * @param roleName The name of the role to filter by.
     * @return A list of UserDTOs belonging to the specified role.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    Page<UserDto> getUsersByRole(ERole roleName, Pageable pageable);

    /**
     * [ADMINISTRATIVE] Changes a user's password directly without knowing the old one.
     * Use with extreme caution and secure appropriately.
     * Consider invalidating user sessions/tokens after this.
     * @param userId The ID of the user whose password will be changed.
     * @param newPassword The new raw password (will be encoded).
     * @throws UserNotFoundException if the user ID is not found.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    void changePasswordAdmin(UUID userId, String newPassword);

    /**
     * [ADMINISTRATIVE] Resets a user's password directly using their email.
     * Bypasses the standard token verification flow. Use with caution.
     * Consider invalidating user sessions/tokens after this.
     * @param email The email of the user whose password will be reset.
     * @param newPassword The new raw password (will be encoded).
     * @throws UserNotFoundException if no user is found with the given email.
     */
    @PreAuthorize("hasRole('ADMIN')") // Example security for admin action
    void resetPasswordAdmin(String email, String newPassword);

}
