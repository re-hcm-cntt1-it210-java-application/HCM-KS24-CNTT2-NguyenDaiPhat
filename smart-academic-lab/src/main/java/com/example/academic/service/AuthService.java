package com.example.academic.service;

import com.example.academic.dto.LoginDto;
import com.example.academic.dto.RegisterDto;
import com.example.academic.entity.Department;
import com.example.academic.entity.LecturerProfile;
import com.example.academic.entity.User;
import com.example.academic.enums.Role;
import com.example.academic.repository.DepartmentRepository;
import com.example.academic.repository.LecturerProfileRepository;
import com.example.academic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final LecturerProfileRepository lecturerProfileRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Transactional
  public User register(RegisterDto dto) {
    if (userRepository.existsByUsername(dto.getUsername()))
      throw new IllegalArgumentException("Tai khoan da ton tai");
    if (userRepository.existsByEmail(dto.getEmail()))
      throw new IllegalArgumentException("Email da duoc su dung");

    Department lecturerDepartment = null;
    if (dto.getRole() == Role.LECTURER) {
      if (dto.getDepartmentId() == null) {
        throw new IllegalArgumentException("Vui long chon khoa/nganh cho giang vien");
      }
      lecturerDepartment = departmentRepository.findById(dto.getDepartmentId())
              .orElseThrow(() -> new IllegalArgumentException("Khoa/nganh khong ton tai"));
    }

    User user = User.builder()
            .username(dto.getUsername())
            .passwordHash(encoder.encode(dto.getPassword()))
            .role(dto.getRole())
            .fullName(dto.getFullName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .build();

    User savedUser = userRepository.save(user);

    if (dto.getRole() == Role.LECTURER) {
      String specialty = hasText(dto.getSpecialty()) ? dto.getSpecialty().trim() : "Chua cap nhat";
      lecturerProfileRepository.save(LecturerProfile.builder()
              .user(savedUser)
              .department(lecturerDepartment)
              .specialty(specialty)
              .build());
    }

    return savedUser;
  }

  public Optional<User> login(LoginDto dto) {
    return userRepository.findByUsername(dto.getUsername())
            .filter(u -> encoder.matches(dto.getPassword(), u.getPasswordHash()));
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
