package com.example.academic.mapper;
import com.example.academic.dto.*;
import com.example.academic.entity.User;
import org.springframework.stereotype.Component;
@Component
public class UserMapper {
  public ProfileDto toProfileDto(User u) { return ProfileDto.builder().fullName(u.getFullName()).email(u.getEmail()).phone(u.getPhone()).build(); }
  public void updateProfile(User u, ProfileDto dto) { u.setFullName(dto.getFullName()); u.setEmail(dto.getEmail()); u.setPhone(dto.getPhone()); }
}
