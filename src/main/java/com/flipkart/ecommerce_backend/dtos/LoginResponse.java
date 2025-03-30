package com.flipkart.ecommerce_backend.dtos;

import com.flipkart.ecommerce_backend.models.LocalUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoginResponse {
  private final String message;
  private final String accessToken;
  private final String refreshToken;
  private final String tokenType = "Bearer";
  private final LocalDateTime timestamp;
  private final UserDto userInfo; // Optional user details


  public static LoginResponse of(String token, LocalUser user) {
    UserDto userInfo = UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();

    return builder()
            .message("Login successful")
            .accessToken(token)
            .refreshToken(token)
            .timestamp(LocalDateTime.now())
            .userInfo(userInfo)
            .build();
  }
}

