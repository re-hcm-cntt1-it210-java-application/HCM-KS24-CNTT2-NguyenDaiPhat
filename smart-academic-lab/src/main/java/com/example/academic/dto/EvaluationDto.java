package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;
@Getter @Setter
public class EvaluationDto {
  @NotNull private Long sessionId;
  @NotBlank private String competencyReview;
  @NotNull @Min(0) @Max(10) private Integer score;
  private List<Long> equipmentIds = new ArrayList<>();
  private List<Integer> quantities = new ArrayList<>();
}
