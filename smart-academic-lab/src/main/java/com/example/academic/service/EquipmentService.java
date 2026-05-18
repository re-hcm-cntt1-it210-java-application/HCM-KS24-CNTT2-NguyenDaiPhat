package com.example.academic.service;
import com.example.academic.dto.EquipmentDto;
import com.example.academic.entity.Equipment;
import com.example.academic.mapper.EquipmentMapper;
import com.example.academic.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
@Service @RequiredArgsConstructor
public class EquipmentService {
  private final EquipmentRepository repo; private final EquipmentMapper mapper;
  public List<EquipmentDto> findAll() { return repo.findAll().stream().map(mapper::toDto).toList(); }
  public EquipmentDto findDto(Long id) { return mapper.toDto(repo.findById(id).orElseThrow()); }
  public void save(EquipmentDto dto) { repo.save(mapper.toEntity(dto)); }
  public void update(Long id, EquipmentDto dto) { Equipment e = repo.findById(id).orElseThrow(); mapper.updateEntity(e, dto); repo.save(e); }
  public void delete(Long id) { repo.deleteById(id); }
}
