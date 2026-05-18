package com.example.academic.service;
import com.example.academic.dto.*;
import com.example.academic.entity.User;
import com.example.academic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service @RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  public User register(RegisterDto dto) {
    if (userRepository.existsByUsername(dto.getUsername())) throw new IllegalArgumentException("Username da ton tai");
    User user = User.builder().username(dto.getUsername()).passwordHash(encoder.encode(dto.getPassword()))
      .role(dto.getRole()).fullName(dto.getFullName()).email(dto.getEmail()).phone(dto.getPhone()).build();
    return userRepository.save(user);
  }
  public Optional<User> login(LoginDto dto) {
    return userRepository.findByUsername(dto.getUsername()).filter(u -> encoder.matches(dto.getPassword(), u.getPasswordHash()));
  }
}
