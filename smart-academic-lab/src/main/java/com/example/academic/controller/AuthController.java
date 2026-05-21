package com.example.academic.controller;
import com.example.academic.dto.*;
import com.example.academic.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller @RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @GetMapping("/") public String home() {
    return "redirect:/login";
  }

  @GetMapping("/login") public String loginForm(Model model) {
    model.addAttribute("loginDto", new LoginDto());
    return "auth/login";
  }

  @PostMapping("/login") public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult br, HttpSession session, Model model) {
    if (br.hasErrors()) return "auth/login";
    var userOpt = authService.login(loginDto);
    if (userOpt.isEmpty()) {
      model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
      return "auth/login";
    }

    var u = userOpt.get();
    session.setAttribute("USER_ID", u.getId());
    session.setAttribute("ROLE", u.getRole());
    session.setAttribute("FULL_NAME", u.getFullName());
    return switch (u.getRole()) {
      case STUDENT -> "redirect:/student/schedule";
      case LECTURER -> "redirect:/lecturer/sessions";
      case ADMIN -> "redirect:/admin/dashboard";
    };
  }
  @GetMapping("/register")
  public String registerForm(Model model) {
    model.addAttribute("registerDto", new RegisterDto());
    return "auth/register";
  }

  @PostMapping("/register") public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult br, Model model) {
    if (br.hasErrors()) return "auth/register";
    try { authService.register(registerDto); return "redirect:/login?registered"; }
    catch (Exception e) { model.addAttribute("error", e.getMessage()); return "auth/register"; }
  }

  @PostMapping("/logout") public String logout(HttpSession session) {
    session.invalidate(); return "redirect:/login";
  }
}
