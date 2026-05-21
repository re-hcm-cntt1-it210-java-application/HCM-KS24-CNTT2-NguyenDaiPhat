package com.example.academic.dto;

import com.example.academic.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterDto {
  @NotBlank(message = "Vui lòng nhập tài khoản")
  private String username;

  @NotBlank(message = "Vui lòng nhập mật khẩu")
  @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
  private String password;

  @NotBlank(message = "Vui lòng nhập họ tên")
  private String fullName;

  @NotBlank(message = "Vui lòng nhập email")
  @Email(message = "Email không đúng định dạng")
  private String email;

  @Pattern(regexp = "^$|^(0\\d{9}|\\+84\\d{9})$", message = "Số điện thoại phải có 10 số bắt đầu bằng 0 hoặc dạng +84")
  private String phone;

  @NotNull(message = "Vui lòng chọn vai trò")
  private Role role;
}
