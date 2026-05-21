package com.example.academic.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
@Getter @Setter
public class ScheduleRequestDto {
  @NotNull(message = "Vui lòng chọn khoa/ngành")
  private Long departmentId;

  @NotNull(message = "Vui lòng chọn giảng viên")
  private Long lecturerId;

  @NotNull(message = "Vui lòng chọn ngày giờ")
  @Future(message = "Không được đặt lịch trong quá khứ")
  private LocalDateTime startTime;
}
