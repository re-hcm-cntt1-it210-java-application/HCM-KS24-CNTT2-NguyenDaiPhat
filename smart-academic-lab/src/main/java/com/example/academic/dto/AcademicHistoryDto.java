package com.example.academic.dto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
@Getter @Setter @Builder
public class AcademicHistoryDto {
  private LocalDateTime startTime;
  private String lecturerName;
  private String competencyReview;
  private Integer score;
  private List<String> borrowedItems;
}
