package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
@Getter @Setter
public class ScheduleRequestDto {
  @NotNull private Long departmentId;
  @NotNull private Long lecturerId;
  @NotNull @Future private LocalDateTime startTime;
}
