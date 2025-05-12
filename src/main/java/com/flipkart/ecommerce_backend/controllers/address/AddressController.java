package com.flipkart.ecommerce_backend.controllers.address;

import com.flipkart.ecommerce_backend.dtos.AddressDTO;
import com.flipkart.ecommerce_backend.dtos.AddressRequestDTO;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.services.AddressService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

  private final AddressService addressService;

  /**
   * Retrieves addresses for the currently authenticated user. Requires authentication. Pagination
   * added.
   *
   * @param userDetails The authenticated user principal.
   * @param pageable Pagination information.
   * @return ResponseEntity containing a Page of AddressDTOs.
   */
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<AddressDTO>> getCurrentUserAddresses(
      @AuthenticationPrincipal UserDetails userDetails,
      @PageableDefault(size = 10) Pageable pageable) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UUID userId = ((LocalUser) userDetails).getId();
    log.info("Request received to get addresses for user ID: {}", userId);

    Page<AddressDTO> addressesPage = addressService.getAddressesForUser(userId, pageable);
    return ResponseEntity.ok(addressesPage);
  }

  /**
   * Adds a new address for the currently authenticated user. Requires authentication.
   *
   * @param userDetails The authenticated user principal.
   * @param addressRequestDTO DTO containing the new address details.
   * @return ResponseEntity containing the created AddressDTO with status 201.
   */
  @PostMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AddressDTO> addAddressForCurrentUser(
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UUID userId = ((LocalUser) userDetails).getId();
    log.info("Request received to add address for user ID: {}", userId);

    AddressDTO savedAddress = addressService.addAddressForUser(userId, addressRequestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
  }

  /*
  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<AddressDTO>> getAddressesForSpecificUser(
          @PathVariable UUID userId,
          @PageableDefault(size = 10) Pageable pageable) {
      log.info("[Admin Action] Request received to get addresses for user ID: {}", userId);
      Page<AddressDTO> addressesPage = addressService.getAddressesForUser(userId, pageable);
      return ResponseEntity.ok(addressesPage);
  }

  @PostMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AddressDTO> addAddressForSpecificUser(
          @PathVariable UUID userId,
          @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
      log.info("[Admin Action] Request received to add address for user ID: {}", userId);
      AddressDTO savedAddress = addressService.addAddressForUser(userId, addressRequestDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
  }
  */

}
