package com.example.academic.controller;

import com.example.academic.dto.ProfileDto;
import com.example.academic.mapper.UserMapper;
import com.example.academic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  private Long uid(HttpSession session) {
    return (Long) session.getAttribute("USER_ID");
  }

  @GetMapping
  public String profile(HttpSession session, Model model) {
    var user = userRepository.findById(uid(session)).orElseThrow();
    model.addAttribute("profileDto", userMapper.toProfileDto(user));
    model.addAttribute("role", user.getRole());
    return "profile";
  }

  @PostMapping
  public String update(@Valid @ModelAttribute ProfileDto profileDto, BindingResult br, HttpSession session, Model model) {
    var user = userRepository.findById(uid(session)).orElseThrow();
    profileDto.setEmail(user.getEmail());
    boolean changingPassword = hasText(profileDto.getCurrentPassword())
            || hasText(profileDto.getNewPassword())
            || hasText(profileDto.getConfirmPassword());

    if (changingPassword) {
      if (!hasText(profileDto.getCurrentPassword())) {
        br.rejectValue("currentPassword", "currentPassword.required", "Vui lòng nhập mật khẩu hiện tại");
      } else if (!encoder.matches(profileDto.getCurrentPassword(), user.getPasswordHash())) {
        br.rejectValue("currentPassword", "currentPassword.invalid", "Mật khẩu hiện tại không đúng");
      }

      if (!hasText(profileDto.getNewPassword())) {
        br.rejectValue("newPassword", "newPassword.required", "Vui lòng nhập mật khẩu mới");
      } else if (profileDto.getNewPassword().length() < 6) {
        br.rejectValue("newPassword", "newPassword.size", "Mật khẩu mới tối thiểu 6 ký tự");
      }

      if (!hasText(profileDto.getConfirmPassword())) {
        br.rejectValue("confirmPassword", "confirmPassword.required", "Vui lòng xác nhận mật khẩu mới");
      } else if (hasText(profileDto.getNewPassword()) && !profileDto.getNewPassword().equals(profileDto.getConfirmPassword())) {
        br.rejectValue("confirmPassword", "confirmPassword.mismatch", "Mật khẩu xác nhận không khớp");
      }
    }

    if (br.hasErrors()) {
      model.addAttribute("role", user.getRole());
      return "profile";
    }

    userMapper.updateProfile(user, profileDto);
    if (changingPassword) {
      user.setPasswordHash(encoder.encode(profileDto.getNewPassword()));
    }
    userRepository.save(user);
    session.setAttribute("FULL_NAME", user.getFullName());
    return "redirect:/profile?saved";
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
