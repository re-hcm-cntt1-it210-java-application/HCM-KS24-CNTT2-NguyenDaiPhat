package com.example.academic.mapper;
import com.example.academic.dto.EquipmentDto;
import com.example.academic.entity.Equipment;
import org.springframework.stereotype.Component;
@Component
public class EquipmentMapper {
  public EquipmentDto toDto(Equipment e) {
    return EquipmentDto.builder()
            .id(e.getId())
            .name(e.getName())
            .unit(e.getUnit())
            .stockQuantity(e.getStockQuantity())
            .deleted(Boolean.TRUE.equals(e.getDeleted()))
            .build();
  }

  public Equipment toEntity(EquipmentDto d) {
    return Equipment.builder()
            .id(d.getId())
            .name(d.getName())
            .unit(d.getUnit())
            .stockQuantity(d.getStockQuantity())
            .deleted(Boolean.TRUE.equals(d.getDeleted()))
            .build();
  }

  public void updateEntity(Equipment e, EquipmentDto d) {
    e.setName(d.getName());
    e.setUnit(d.getUnit());
    e.setStockQuantity(d.getStockQuantity());
    e.setDeleted(Boolean.TRUE.equals(d.getDeleted()));
  }
}
