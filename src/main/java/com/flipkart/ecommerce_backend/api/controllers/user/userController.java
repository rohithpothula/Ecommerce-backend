package com.flipkart.ecommerce_backend.api.controllers.user;

import com.flipkart.ecommerce_backend.api.models.GenericResponseBody;
import com.flipkart.ecommerce_backend.models.Address;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.services.UserService;
import java.util.List;
import java.util.Optional;
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

@RestController
@RequestMapping("/user")
public class userController {

  @Autowired private UserService userService;

  @Autowired private LocalUserRepository localUserRepository;

  @GetMapping("/address/{userId}")
  public ResponseEntity<List<Address>> getAddress(
      @PathVariable Long userId, @AuthenticationPrincipal LocalUser localuser) {
    GenericResponseBody genericResponseBody = new GenericResponseBody();
    if (localuser.getId() == userId) {
      List<Address> addresses = userService.getAddress(userId);
      genericResponseBody.setIsSuccess(true);
      return ResponseEntity.status(HttpStatus.OK).body(addresses);
    } else {
      genericResponseBody.setIsSuccess(false);
      genericResponseBody.setFailureReason("USER_IS_NOT_AUTHORIZED_TO_ACCESS");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PostMapping("/address/{userId}")
  public ResponseEntity<Address> saveAddress(
      @PathVariable Long userId,
      @AuthenticationPrincipal LocalUser localUser,
      @RequestBody Address address) {
    GenericResponseBody genericResponseBody = new GenericResponseBody();
    if (localUser.getId() == userId) {
      genericResponseBody.setIsSuccess(true);
      Optional<LocalUser> opLocalUser = localUserRepository.findById(userId);
      if (opLocalUser.isPresent()) {
        LocalUser tempUser = opLocalUser.get();
        address.setLocalUser(tempUser);
        Address address1 = userService.saveAddress(address);
        return ResponseEntity.status(HttpStatus.OK).body(address1);
      } else {
        genericResponseBody.setIsSuccess(false);
        genericResponseBody.setFailureReason("INVALID_USER_ID");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } else {
      genericResponseBody.setIsSuccess(false);
      genericResponseBody.setFailureReason("USER_IS_NOT_AUTHORIZED_TO_ACCESS");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}
