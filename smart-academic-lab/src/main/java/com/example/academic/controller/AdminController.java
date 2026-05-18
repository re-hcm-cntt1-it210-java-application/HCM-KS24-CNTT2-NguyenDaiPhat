package com.example.academic.controller;
import com.example.academic.dto.EquipmentDto;
import com.example.academic.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {
  private final EquipmentService equipmentService; private final BorrowingService borrowingService;
  @GetMapping("/equipments") public String equipments(Model model) { model.addAttribute("equipments", equipmentService.findAll()); return "admin/equipments"; }
  @GetMapping("/equipments/new") public String newEquipment(Model model) { model.addAttribute("equipmentDto", new EquipmentDto()); return "admin/equipment-form"; }
  @PostMapping("/equipments") public String create(@Valid @ModelAttribute EquipmentDto dto, BindingResult br) { if (br.hasErrors()) return "admin/equipment-form"; equipmentService.save(dto); return "redirect:/admin/equipments"; }
  @GetMapping("/equipments/{id}/edit") public String edit(@PathVariable Long id, Model model) { model.addAttribute("equipmentDto", equipmentService.findDto(id)); return "admin/equipment-form"; }
  @PostMapping("/equipments/{id}") public String update(@PathVariable Long id, @Valid @ModelAttribute EquipmentDto dto, BindingResult br) { if (br.hasErrors()) return "admin/equipment-form"; equipmentService.update(id, dto); return "redirect:/admin/equipments"; }
  @PostMapping("/equipments/{id}/delete") public String delete(@PathVariable Long id) { equipmentService.delete(id); return "redirect:/admin/equipments"; }
  @GetMapping("/borrowings") public String borrowings(Model model) { model.addAttribute("records", borrowingService.waitingRecords()); return "admin/borrowings"; }
  @PostMapping("/borrowings/{id}/issue") public String issue(@PathVariable Long id) { try { borrowingService.issue(id); return "redirect:/admin/borrowings?success"; } catch (Exception e) { return "redirect:/admin/borrowings?error=" + e.getMessage().replace(" ", "+"); } }
}
