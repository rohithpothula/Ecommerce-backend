package com.flipkart.ecommerce_backend.services;


import com.flipkart.ecommerce_backend.dtos.AddressDTO;
import com.flipkart.ecommerce_backend.dtos.AddressRequestDTO;
import com.flipkart.ecommerce_backend.exception.auth.UnauthorizedAccessException;
import com.flipkart.ecommerce_backend.exception.address.AddressNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

/**
 * Service interface for managing user addresses.
 */
@Validated
public interface AddressService {

    /**
     * Retrieves a paginated list of addresses associated with a specific user.
     *
     * @param userId   The ID of the user whose addresses are to be retrieved.
     * @param pageable Pagination information.
     * @return A Page of AddressDTOs.
     * @throws com.flipkart.ecommerce_backend.exception.user.UserNotFoundException if the user doesn't exist (optional check).
     */
    Page<AddressDTO> getAddressesForUser(UUID userId, Pageable pageable);

    /**
     * Adds a new address for the specified user.
     *
     * @param userId            The ID of the user to associate the address with.
     * @param addressRequestDTO DTO containing the new address details. Must be valid.
     * @return The created AddressDTO.
     * @throws com.flipkart.ecommerce_backend.exception.user.UserNotFoundException if the user doesn't exist.
     * @throws com.flipkart.ecommerce_backend.exception.DatabaseException on persistence errors.
     */
    AddressDTO addAddressForUser(UUID userId, @Valid AddressRequestDTO addressRequestDTO);

    /**
     * Retrieves a specific address by its ID.
     * Includes an optional check to ensure the address belongs to the specified user if needed.
     *
     * @param addressId The ID of the address to retrieve.
     * @param userId    (Optional) The ID of the user who should own the address. If provided, verifies ownership.
     * @return The AddressDTO.
     * @throws AddressNotFoundException if the address is not found.
     * @throws UnauthorizedAccessException if userId is provided and doesn't match the address owner.
     */
    AddressDTO getAddressById(UUID addressId, UUID userId);

    /**
     * Updates an existing address.
     * Includes an optional check to ensure the address belongs to the specified user before updating.
     *
     * @param addressId         The ID of the address to update.
     * @param userId            The ID of the user who must own the address.
     * @param addressRequestDTO DTO containing the updated address details. Must be valid.
     * @return The updated AddressDTO.
     * @throws AddressNotFoundException if the address is not found.
     * @throws UnauthorizedAccessException if the address does not belong to the specified user.
     * @throws com.flipkart.ecommerce_backend.exception.DatabaseException on persistence errors.
     */
    AddressDTO updateAddressForUser(UUID addressId, UUID userId, @Valid AddressRequestDTO addressRequestDTO);

    /**
     * Deletes an address by its ID.
     * Includes an optional check to ensure the address belongs to the specified user before deleting.
     *
     * @param addressId The ID of the address to delete.
     * @param userId    The ID of the user who must own the address.
     * @throws AddressNotFoundException if the address is not found.
     * @throws UnauthorizedAccessException if the address does not belong to the specified user.
     */
    void deleteAddressForUser(UUID addressId, UUID userId);

}
