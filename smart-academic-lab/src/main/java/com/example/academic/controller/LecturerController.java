package com.example.academic.controller;
import com.example.academic.dto.EvaluationDto;
import com.example.academic.repository.EquipmentRepository;
import com.example.academic.service.MentoringService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/lecturer") @RequiredArgsConstructor
public class LecturerController {
  private final MentoringService mentoringService; private final EquipmentRepository equipmentRepository;
  private Long uid(HttpSession s) { return (Long) s.getAttribute("USER_ID"); }
  @GetMapping("/sessions") public String sessions(HttpSession session, Model model) { model.addAttribute("sessions", mentoringService.pendingForLecturer(uid(session))); return "lecturer/sessions"; }
  @GetMapping("/sessions/{id}/evaluate") public String evaluateForm(@PathVariable Long id, Model model) { EvaluationDto dto = new EvaluationDto(); dto.setSessionId(id); model.addAttribute("evaluationDto", dto); model.addAttribute("equipments", equipmentRepository.findAll()); return "lecturer/evaluate"; }
  @PostMapping("/evaluate") public String evaluate(@Valid @ModelAttribute EvaluationDto dto, BindingResult br, Model model) {
    if (br.hasErrors()) { model.addAttribute("equipments", equipmentRepository.findAll()); return "lecturer/evaluate"; }
    try { mentoringService.evaluate(dto); return "redirect:/lecturer/sessions?success"; }
    catch (Exception e) { model.addAttribute("error", e.getMessage()); model.addAttribute("equipments", equipmentRepository.findAll()); return "lecturer/evaluate"; }
  }
}
