package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileDto {
    @NotBlank(message = "Vui lòng nhập họ tên")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^$|^(0\\d{9}|\\+84\\d{9})$", message = "Số điện thoại phải có 10 số bắt đầu bằng 0 hoặc dạng +84")
    private String phone;

    private String currentPassword;

    private String newPassword;

    private String confirmPassword;
}
