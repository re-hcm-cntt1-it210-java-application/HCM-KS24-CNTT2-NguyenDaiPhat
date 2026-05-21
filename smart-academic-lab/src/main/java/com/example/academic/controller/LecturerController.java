package com.example.academic.controller;
import com.example.academic.dto.EvaluationDto;
import com.example.academic.service.EquipmentService;
import com.example.academic.service.MentoringService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller @RequestMapping("/lecturer")
@RequiredArgsConstructor
public class LecturerController {
  private final MentoringService mentoringService;
  private final EquipmentService equipmentService;

  private Long uid(HttpSession s) {
    return (Long) s.getAttribute("USER_ID");
  }

  @GetMapping("/sessions") public String sessions(HttpSession session, Model model) {
    model.addAttribute("sessions", mentoringService.pendingForLecturer(uid(session)));
    model.addAttribute("now", LocalDateTime.now());
    return "lecturer/sessions";
  }

  @GetMapping("/sessions/{id}/evaluate")
  public String evaluateForm(@PathVariable Long id, HttpSession session, Model model) {
    try {
      mentoringService.evaluableSessionForLecturer(id, uid(session));
    } catch (Exception e) {
      return "redirect:/lecturer/sessions";
    }
    EvaluationDto dto = new EvaluationDto(); dto.setSessionId(id);

    model.addAttribute("evaluationDto", dto);
    model.addAttribute("equipments", equipmentService.findBorrowable());
    return "lecturer/evaluate";
  }

  @PostMapping("/evaluate") public String evaluate(@Valid @ModelAttribute EvaluationDto dto, BindingResult br, HttpSession session, Model model) {
    if (br.hasErrors()) { model.addAttribute("equipments", equipmentService.findBorrowable());
      return "lecturer/evaluate";
    }

    try { mentoringService.evaluate(uid(session), dto);
      return "redirect:/lecturer/sessions?success";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("equipments", equipmentService.findBorrowable());
      return "lecturer/evaluate";
    }
  }
}
