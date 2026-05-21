package com.example.academic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "invalidated_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvalidatedToken {
  @Id
  private String id;

  private Instant expiryTime;
}
