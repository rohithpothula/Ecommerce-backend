package com.flipkart.ecommerce_backend.api.models;

import com.flipkart.ecommerce_backend.models.LocalUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
  private final String message;
  private final String token;
  private final String tokenType = "Bearer";
  private final LocalDateTime timestamp;
  private final UserInfo userInfo; // Optional user details

  @Getter
  @Builder
  public static class UserInfo {
    private final UUID id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
  }

  public static LoginResponse of(String token, LocalUser user) {
    UserInfo userInfo = UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();

    return builder()
            .message("Login successful")
            .token(token)
            .timestamp(LocalDateTime.now())
            .userInfo(userInfo)
            .build();
  }
}

