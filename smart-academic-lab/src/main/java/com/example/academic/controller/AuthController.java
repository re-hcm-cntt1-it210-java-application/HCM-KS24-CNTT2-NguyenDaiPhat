package com.example.academic.controller;

import com.example.academic.dto.AuthResponse;
import com.example.academic.dto.LoginDto;
import com.example.academic.dto.RegisterDto;
import com.example.academic.repository.DepartmentRepository;
import com.example.academic.service.AuthService;
import com.example.academic.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtService jwtService;
  private final DepartmentRepository departmentRepository;

  @GetMapping("/")
  public String home() {
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String loginForm(Model model) {
    model.addAttribute("loginDto", new LoginDto());
    return "auth/login";
  }

  @PostMapping("/login")
  public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult br, HttpSession session, Model model) {
    if (br.hasErrors()) return "auth/login";
    var userOpt = authService.login(loginDto);
    if (userOpt.isEmpty()) {
      model.addAttribute("error", "Sai tai khoan hoac mat khau");
      return "auth/login";
    }

    var user = userOpt.get();
    String token = jwtService.generateToken(user);
    session.setAttribute("ACCESS_TOKEN", token);
    session.setAttribute("USER_ID", user.getId());
    session.setAttribute("ROLE", user.getRole());
    session.setAttribute("FULL_NAME", user.getFullName());

    return switch (user.getRole()) {
      case STUDENT -> "redirect:/student/schedule";
      case LECTURER -> "redirect:/lecturer/sessions";
      case ADMIN -> "redirect:/admin/dashboard";
    };
  }

  @GetMapping("/register")
  public String registerForm(Model model) {
    model.addAttribute("registerDto", new RegisterDto());
    addRegisterAttributes(model);
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult br, Model model) {
    if (br.hasErrors()) {
      addRegisterAttributes(model);
      return "auth/register";
    }
    try {
      authService.register(registerDto);
      return "redirect:/login?registered";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      addRegisterAttributes(model);
      return "auth/register";
    }
  }

  @PostMapping("/logout")
  public String logout(HttpSession session) {
    Object token = session.getAttribute("ACCESS_TOKEN");
    if (token instanceof String accessToken) {
      try {
        jwtService.logout(accessToken);
      } catch (JwtException | IllegalArgumentException ignored) {
        // Token may already be expired; the session is still removed below.
      }
    }
    session.invalidate();
    return "redirect:/login";
  }

  @PostMapping("/api/auth/login")
  @ResponseBody
  public ResponseEntity<?> apiLogin(@Valid @RequestBody LoginDto loginDto) {
    var userOpt = authService.login(loginDto);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("error", "Sai tai khoan hoac mat khau"));
    }

    String token = jwtService.generateToken(userOpt.get());
    return ResponseEntity.ok(new AuthResponse("Bearer", token, jwtService.accessTokenSeconds()));
  }

  @PostMapping("/api/auth/logout")
  @ResponseBody
  public ResponseEntity<?> apiLogout(
          @RequestHeader(name = "Authorization", required = false) String authorization,
          HttpServletRequest request) {
    jwtService.logout(extractToken(authorization, request.getSession(false)));
    return ResponseEntity.ok(Map.of("message", "Logged out"));
  }

  @PostMapping("/api/auth/refresh")
  @ResponseBody
  public ResponseEntity<?> apiRefresh(
          @RequestHeader(name = "Authorization", required = false) String authorization,
          HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    String newToken = jwtService.refreshToken(extractToken(authorization, session));
    if (session != null) {
      session.setAttribute("ACCESS_TOKEN", newToken);
    }
    return ResponseEntity.ok(new AuthResponse("Bearer", newToken, jwtService.refreshedTokenSeconds()));
  }

  private String extractToken(String authorization, HttpSession session) {
    if (authorization != null && authorization.startsWith("Bearer ")) {
      return authorization.substring(7);
    }
    if (authorization != null && !authorization.isBlank()) {
      return authorization;
    }
    if (session != null && session.getAttribute("ACCESS_TOKEN") instanceof String token) {
      return token;
    }
    throw new IllegalArgumentException("Missing bearer token");
  }

  private void addRegisterAttributes(Model model) {
    model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
  }
}
