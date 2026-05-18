package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileDto { @NotBlank private String fullName; @Email private String email; private String phone; }
