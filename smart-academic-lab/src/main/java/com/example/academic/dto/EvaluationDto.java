package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;
@Getter @Setter
public class EvaluationDto {
  @NotNull
  private Long sessionId;

  @NotBlank(message = "Vui lòng nhập đánh giá năng lực")
  private String competencyReview;

  @NotNull(message = "Vui lòng nhập điểm")
  @Min(value = 0, message = "Điểm thấp nhất là 0")
  @Max(value = 10, message = "Điểm cao nhất là 10")
  private Integer score;

  private List<Long> equipmentIds = new ArrayList<>();
  private List<Integer> quantities = new ArrayList<>();
}
