package com.flipkart.ecommerce_backend.services.impl;


import com.flipkart.ecommerce_backend.dtos.AddressDTO;
import com.flipkart.ecommerce_backend.dtos.AddressRequestDTO;
import com.flipkart.ecommerce_backend.exception.DatabaseException;
import com.flipkart.ecommerce_backend.exception.address.AddressNotFoundException;
import com.flipkart.ecommerce_backend.exception.auth.UnauthorizedAccessException;
import com.flipkart.ecommerce_backend.exception.user.UserNotFoundException;
import com.flipkart.ecommerce_backend.models.Address;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.repository.AddressRepository;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.services.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final LocalUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AddressDTO> getAddressesForUser(UUID userId, Pageable pageable) {
        log.debug("Fetching addresses for user ID: {}", userId);
         findUserByIdInternal(userId);

        Page<Address> addressPage = addressRepository.findByLocalUser_Id(userId, pageable);
        log.debug("Found {} addresses on page {} for user ID: {}", addressPage.getNumberOfElements(), pageable.getPageNumber(), userId);
        return addressPage.map(this::mapToAddressDTO);
    }

    @Override
    @Transactional
    public AddressDTO addAddressForUser(UUID userId, AddressRequestDTO addressRequestDTO) {
        log.info("Adding address for user ID: {}", userId);
        LocalUser user = findUserByIdInternal(userId);

        Address address = mapToAddressEntity(addressRequestDTO);
        address.setLocalUser(user);

        try {
            Address savedAddress = addressRepository.save(address);
            log.info("Successfully added address ID: {} for user ID: {}", savedAddress.getId(), userId);
            return mapToAddressDTO(savedAddress);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to save address for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to save address", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(UUID addressId, UUID userId) {
        log.debug("Fetching address by ID: {}", addressId);
        Address address = findAddressByIdInternal(addressId);

        if (userId != null && !address.getLocalUser().getId().equals(userId)) {
            log.warn("User {} attempted to access address {} owned by user {}", userId, addressId, address.getLocalUser().getId());
            throw new UnauthorizedAccessException("User does not have permission to access this address.");
        }
        return mapToAddressDTO(address);
    }


    @Override
    @Transactional
    public AddressDTO updateAddressForUser(UUID addressId, UUID userId, AddressRequestDTO addressRequestDTO) {
        log.info("Updating address ID: {} for user ID: {}", addressId, userId);
        Address address = findAddressByIdInternal(addressId);

        if (!address.getLocalUser().getId().equals(userId)) {
            log.warn("User {} attempted to update address {} owned by user {}", userId, addressId, address.getLocalUser().getId());
            throw new UnauthorizedAccessException("User does not have permission to update this address.");
        }

        address.setAddressLine1(addressRequestDTO.addressLine1());
        address.setAddressLine2(addressRequestDTO.addressLine2());
        address.setCity(addressRequestDTO.city());
        address.setState(addressRequestDTO.state());
        address.setCountry(addressRequestDTO.country());
        address.setPostalCode(addressRequestDTO.postalCode());

        try {
            Address updatedAddress = addressRepository.save(address);
            log.info("Successfully updated address ID: {}", addressId);
            return mapToAddressDTO(updatedAddress);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to update address {}: {}", addressId, e.getMessage());
            throw new DatabaseException("Failed to update address", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAddressForUser(UUID addressId, UUID userId) {
        log.warn("Attempting to delete address ID: {} for user ID: {}", addressId, userId);
        Address address = findAddressByIdInternal(addressId);

        if (!address.getLocalUser().getId().equals(userId)) {
            log.warn("User {} attempted to delete address {} owned by user {}", userId, addressId, address.getLocalUser().getId());
            throw new UnauthorizedAccessException("User does not have permission to delete this address.");
        }

        try {
            addressRepository.delete(address);
            log.warn("Successfully deleted address ID: {}", addressId);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to delete address {} due to constraints: {}", addressId, e.getMessage());
            throw new DatabaseException("Cannot delete address, it might be in use.", e.getMessage());
        }
    }


    private LocalUser findUserByIdInternal(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    private Address findAddressByIdInternal(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId.toString()));
    }


    private AddressDTO mapToAddressDTO(Address address) {
        if (address == null) return null;
        return new AddressDTO(
                address.getId(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode()
        );
    }

    private Address mapToAddressEntity(AddressRequestDTO dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setAddressLine1(dto.addressLine1());
        address.setAddressLine2(dto.addressLine2());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setPostalCode(dto.postalCode());
        return address;
    }
}
