package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EquipmentDto {
    private Long id;

    @NotBlank private
    String name; private

    String unit; @NotNull

    @Min(0) private
    Integer stockQuantity; }
