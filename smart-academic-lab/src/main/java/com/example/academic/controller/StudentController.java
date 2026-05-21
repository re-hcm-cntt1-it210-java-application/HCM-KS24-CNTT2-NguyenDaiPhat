package com.example.academic.controller;
import com.example.academic.dto.ScheduleRequestDto;
import com.example.academic.service.MentoringService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
  private final MentoringService mentoringService;

  private Long uid(HttpSession s) {
    Object sessionUserId = s.getAttribute("USER_ID");
    if (sessionUserId instanceof Long userId) return userId;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      Object claimUserId = jwtAuthentication.getToken().getClaim("userId");
      if (claimUserId instanceof Number number) return number.longValue();
    }

    throw new IllegalStateException("Cannot find current user id");
  }

  @GetMapping("/schedule") public String scheduleForm(Model model) {
    model.addAttribute("scheduleRequestDto", new ScheduleRequestDto());
    model.addAttribute("departments", mentoringService.departments());
    model.addAttribute("lecturerProfiles", mentoringService.lecturerProfiles());
    return "student/schedule";
  }

  @PostMapping("/schedule") public String schedule(@Valid @ModelAttribute ScheduleRequestDto dto, BindingResult br, HttpSession session, Model model) {
    if (br.hasErrors()) {
      model.addAttribute("departments", mentoringService.departments());
      model.addAttribute("lecturerProfiles", mentoringService.lecturerProfiles());
      return "student/schedule";
    }
    try { mentoringService.schedule(uid(session), dto);
      return "redirect:/student/sessions?success";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("departments", mentoringService.departments());
      model.addAttribute("lecturerProfiles", mentoringService.lecturerProfiles());
      return "student/schedule";
    }
  }

  @GetMapping("/sessions") public String sessions(HttpSession session, Model model) {
    Long studentId = uid(session);
    model.addAttribute("sessions", mentoringService.mySessions(studentId));
    model.addAttribute("borrowStatuses", mentoringService.borrowingStatuses(studentId));
    return "student/sessions";
  }

  @PostMapping("/sessions/{id}/cancel")
  public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      mentoringService.cancel(id, uid(session));
      return "redirect:/student/sessions?cancelled";
    } catch (Exception e) {
      redirectAttributes.addAttribute("error", e.getMessage());
      return "redirect:/student/sessions";
    }
  }

  @GetMapping("/history") public String history(HttpSession session, Model model) {
    model.addAttribute("histories", mentoringService.academicHistory(uid(session)));
    return "student/history";
  }
}
