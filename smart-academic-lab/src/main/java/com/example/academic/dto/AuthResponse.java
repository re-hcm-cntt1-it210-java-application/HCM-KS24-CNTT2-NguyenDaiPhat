package com.example.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
  private String tokenType;
  private String token;
  private long expiresInSeconds;
}
