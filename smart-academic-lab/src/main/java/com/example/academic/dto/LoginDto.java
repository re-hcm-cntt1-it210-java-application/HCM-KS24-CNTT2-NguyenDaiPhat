package com.example.academic.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter @Setter
public class LoginDto { @NotBlank private String username; @NotBlank private String password; }
