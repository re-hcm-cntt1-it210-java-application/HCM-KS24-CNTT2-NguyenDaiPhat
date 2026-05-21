package com.example.academic.controller;
import com.example.academic.dto.EquipmentDto;
import com.example.academic.repository.DepartmentRepository;
import com.example.academic.repository.LabRoomTypeRepository;
import com.example.academic.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final EquipmentService equipmentService;
  private final BorrowingService borrowingService;
  private final DashboardService dashboardService;
  private final DepartmentRepository departmentRepository;
  private final LabRoomTypeRepository labRoomTypeRepository;

  @GetMapping({"", "/dashboard"})
  public String dashboard(Model model) {
    model.addAttribute("totalIssuedEquipmentQuantity", dashboardService.totalIssuedEquipmentQuantity());
    model.addAttribute("totalWaitingIssueEquipmentQuantity", dashboardService.totalWaitingIssueEquipmentQuantity());
    model.addAttribute("waitingIssueRecordCount", dashboardService.waitingIssueRecordCount());
    model.addAttribute("completedSessionCount", dashboardService.completedSessionCount());
    model.addAttribute("topLecturers", dashboardService.topLecturers());
    model.addAttribute("maxLecturerConsultationCount", dashboardService.maxLecturerConsultationCount());
    return "admin/dashboard";
  }

  @GetMapping("/equipments")
  public String equipments(Model model) {
    model.addAttribute("equipments", equipmentService.findAll());
    return "admin/equipments";
  }

  @GetMapping("/equipments/new")
  public String newEquipment(Model model) {
    model.addAttribute("equipmentDto", new EquipmentDto());
    return "admin/equipment-form";
  }

  @PostMapping("/equipments")
  public String create(@Valid @ModelAttribute EquipmentDto dto, BindingResult br) {
    if (br.hasErrors())
      return "admin/equipment-form";

    equipmentService.save(dto);
    return "redirect:/admin/equipments";
  }

  @GetMapping("/equipments/{id}/edit")
  public String edit(@PathVariable Long id, Model model) {
    model.addAttribute("equipmentDto", equipmentService.findDto(id));
    return "admin/equipment-form";
  }

  @PostMapping("/equipments/{id}")
  public String update(@PathVariable Long id, @Valid @ModelAttribute EquipmentDto dto, BindingResult br) {
    if (br.hasErrors()) return "admin/equipment-form";
    equipmentService.update(id, dto);
    return "redirect:/admin/equipments";
  }

  @PostMapping("/equipments/{id}/delete")
  public String delete(@PathVariable Long id) {
    equipmentService.delete(id);
    return "redirect:/admin/equipments";
  }

  @PostMapping("/equipments/{id}/restore")
  public String restore(@PathVariable Long id) {
    equipmentService.restore(id);
    return "redirect:/admin/equipments";
  }

  @GetMapping("/foundation-data")
  public String foundationData(Model model) {
    model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
    model.addAttribute("labRoomTypes", labRoomTypeRepository.findAllByOrderByNameAsc());
    return "admin/foundation-data";
  }

  @GetMapping("/borrowings")
  public String borrowings(Model model) {
    model.addAttribute("records", borrowingService.waitingRecords());
    return "admin/borrowings";
  }

  @PostMapping("/borrowings/{id}/issue")
  public String issue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try { borrowingService.issue(id);
      return "redirect:/admin/borrowings?success";
    } catch (Exception e) {
      redirectAttributes.addAttribute("error", e.getMessage());
      return "redirect:/admin/borrowings";
    }
  }
}
