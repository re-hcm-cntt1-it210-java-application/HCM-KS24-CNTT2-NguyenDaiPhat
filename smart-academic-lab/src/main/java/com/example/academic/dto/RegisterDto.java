package com.example.academic.dto;

import com.example.academic.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterDto {
  @NotBlank(message = "Vui long nhap tai khoan")
  private String username;

  @NotBlank(message = "Vui long nhap mat khau")
  @Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
  private String password;

  @NotBlank(message = "Vui long nhap ho ten")
  private String fullName;

  @NotBlank(message = "Vui long nhap email")
  @Email(message = "Email khong dung dinh dang")
  private String email;

  @Pattern(regexp = "^$|^(0\\d{9}|\\+84\\d{9})$", message = "So dien thoai phai co 10 so bat dau bang 0 hoac dang +84")
  private String phone;

  @NotNull(message = "Vui long chon vai tro")
  private Role role;

  private Long departmentId;

  @Size(max = 100, message = "Chuyen mon toi da 100 ky tu")
  private String specialty;
}
