package com.example.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDto {
  @NotBlank(message = "Vui lòng nhập tài khoản")
  private String username;

  @NotBlank(message = "Vui lòng nhập mật khẩu")
  private String password;
}
