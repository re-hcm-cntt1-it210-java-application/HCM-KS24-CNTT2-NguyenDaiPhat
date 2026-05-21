package com.example.academic.mapper;
import com.example.academic.dto.AcademicHistoryDto;
import com.example.academic.entity.*;
import com.example.academic.repository.*;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.*;
@Component @RequiredArgsConstructor
public class AcademicHistoryMapper {
  private final AcademicEvaluationRepository evaluationRepository;
  private final BorrowingRecordRepository borrowingRecordRepository;

  public AcademicHistoryDto toDto(MentoringSession s) {
    var ev = evaluationRepository.findBySessionId(s.getId()).orElse(null);
    var br = borrowingRecordRepository.findBySessionId(s.getId()).orElse(null);
    List<String> items = new ArrayList<>();
    if (br != null)
      for (BorrowingDetail d : br.getDetails())
        items.add(d.getEquipment().getName() + " x " + d.getQuantity());

    return AcademicHistoryDto.builder()
      .startTime(s.getStartTime()).lecturerName(s.getLecturer().getFullName())
      .competencyReview(ev == null ? "" : ev.getCompetencyReview())
      .score(ev == null ? null : ev.getScore()).borrowedItems(items).build();
  }
}
