package com.example.academic.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EquipmentDto {
  private Long id;

  @NotBlank(message = "Vui lòng nhập tên thiết bị")
  private String name;

  private String unit;

  @NotNull(message = "Vui lòng nhập số lượng tồn")
  @Min(value = 0, message = "Số lượng tồn không được âm")
  private Integer stockQuantity;

  @Builder.Default
  private Boolean deleted = false;
}
