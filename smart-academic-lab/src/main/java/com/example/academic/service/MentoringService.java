package com.example.academic.service;
import com.example.academic.dto.*;
import com.example.academic.entity.*;
import com.example.academic.enums.*;
import com.example.academic.mapper.AcademicHistoryMapper;
import com.example.academic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service @RequiredArgsConstructor
public class MentoringService {
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final MentoringSessionRepository sessionRepository;
  private final EquipmentRepository equipmentRepository;
  private final AcademicEvaluationRepository evaluationRepository;
  private final BorrowingRecordRepository borrowingRecordRepository;
  private final AcademicHistoryMapper historyMapper;
  @Value("${app.cancel-before-hours:24}") private long cancelBeforeHours;
  public List<Department> departments() { return departmentRepository.findAll(); }
  public List<User> lecturers() { return userRepository.findByRole(Role.LECTURER); }
  public MentoringSession schedule(Long studentId, ScheduleRequestDto dto) {
    if (dto.getStartTime().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Khong duoc dat lich trong qua khu");
    if (sessionRepository.existsByLecturerIdAndStartTimeAndStatusNot(dto.getLecturerId(), dto.getStartTime(), SessionStatus.CANCELLED)) throw new IllegalArgumentException("Giang vien da co lich o khung gio nay");
    User student = userRepository.findById(studentId).orElseThrow();
    User lecturer = userRepository.findById(dto.getLecturerId()).orElseThrow();
    MentoringSession s = MentoringSession.builder().student(student).lecturer(lecturer).startTime(dto.getStartTime()).endTime(dto.getStartTime().plusHours(1)).status(SessionStatus.PENDING).build();
    return sessionRepository.save(s);
  }
  public List<MentoringSession> mySessions(Long studentId) { return sessionRepository.findByStudentIdOrderByStartTimeDesc(studentId); }
  public List<MentoringSession> pendingForLecturer(Long lecturerId) { return sessionRepository.findByLecturerIdAndStatusOrderByStartTimeAsc(lecturerId, SessionStatus.PENDING); }
  @Transactional
  public void evaluate(EvaluationDto dto) {
    MentoringSession s = sessionRepository.findById(dto.getSessionId()).orElseThrow();
    if (s.getStatus() != SessionStatus.PENDING) throw new IllegalArgumentException("Lich khong o trang thai cho xu ly");
    s.setStatus(SessionStatus.COMPLETED);
    evaluationRepository.save(AcademicEvaluation.builder().session(s).competencyReview(dto.getCompetencyReview()).score(dto.getScore()).build());
    BorrowingRecord record = BorrowingRecord.builder().session(s).status(BorrowStatus.WAITING_ISSUE).build();
    for (int i = 0; i < dto.getEquipmentIds().size(); i++) {
      Long eqId = dto.getEquipmentIds().get(i);
      Integer qty = dto.getQuantities().size() > i ? dto.getQuantities().get(i) : 1;
      if (eqId == null || qty == null || qty <= 0) continue;
      Equipment equipment = equipmentRepository.findById(eqId).orElseThrow();
      BorrowingDetail detail = BorrowingDetail.builder().record(record).equipment(equipment).quantity(qty).build();
      record.getDetails().add(detail);
    }
    borrowingRecordRepository.save(record);
  }
  public List<AcademicHistoryDto> academicHistory(Long studentId) { return sessionRepository.completedHistory(studentId).stream().map(historyMapper::toDto).toList(); }
  @Transactional
  public void cancel(Long sessionId, Long studentId) {
    MentoringSession s = sessionRepository.findById(sessionId).orElseThrow();
    if (!s.getStudent().getId().equals(studentId)) throw new IllegalArgumentException("Khong co quyen huy lich nay");
    if (LocalDateTime.now().isAfter(s.getStartTime().minusHours(cancelBeforeHours))) throw new IllegalArgumentException("Da qua gioi han thoi gian cho phep huy");
    s.setStatus(SessionStatus.CANCELLED);
  }
}
