package com.example.academic.service;

import com.example.academic.dto.TopLecturerStat;
import com.example.academic.repository.BorrowingRecordRepository;
import com.example.academic.repository.MentoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
  private final BorrowingRecordRepository borrowingRecordRepository;
  private final MentoringSessionRepository mentoringSessionRepository;

  public Long totalIssuedEquipmentQuantity() {
    return borrowingRecordRepository.totalIssuedEquipmentQuantity();
  }

  public Long totalWaitingIssueEquipmentQuantity() {
    return borrowingRecordRepository.totalWaitingIssueEquipmentQuantity();
  }

  public Long waitingIssueRecordCount() {
    return borrowingRecordRepository.countWaitingIssueRecords();
  }

  public Long completedSessionCount() {
    return mentoringSessionRepository.countCompletedSessions();
  }

  public List<TopLecturerStat> topLecturers() {
    return mentoringSessionRepository.topLecturersByCompletedConsultations(PageRequest.of(0, 5));
  }

  public Long maxLecturerConsultationCount() {
    return mentoringSessionRepository.maxCompletedConsultationCount();
  }
}
