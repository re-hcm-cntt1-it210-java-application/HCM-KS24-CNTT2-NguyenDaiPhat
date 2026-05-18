package com.example.academic.dto;
import com.example.academic.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter
public class RegisterDto {
  @NotBlank private String username;
  @NotBlank @Size(min = 6) private String password;
  @NotBlank private String fullName;
  @Email private String email;
  private String phone;
  @NotNull private Role role;
}
