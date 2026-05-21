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
  private final LecturerProfileRepository lecturerProfileRepository;
  private final MentoringSessionRepository sessionRepository;
  private final EquipmentRepository equipmentRepository;
  private final AcademicEvaluationRepository evaluationRepository;
  private final BorrowingRecordRepository borrowingRecordRepository;
  private final AcademicHistoryMapper historyMapper;

  @Value("${app.cancel-before-hours:24}") private long cancelBeforeHours;

  public List<Department> departments() {
    return departmentRepository.findAllByOrderByNameAsc();
  }

  public List<User> lecturers() {
    return userRepository.findByRole(Role.LECTURER);
  }

  public List<LecturerProfile> lecturerProfiles() {
    return lecturerProfileRepository.findAllForSchedule();
  }

  public MentoringSession schedule(Long studentId, ScheduleRequestDto dto) {
    if (dto.getStartTime().isBefore(LocalDateTime.now()))
      throw new IllegalArgumentException("Không được đặt lịch trong quá khứ");

    if (dto.getStartTime().getMinute() != 0 || dto.getStartTime().getSecond() != 0 || dto.getStartTime().getNano() != 0)
      throw new IllegalArgumentException("Vui lòng chọn khung giờ tròn 1 giờ");

    var lecturerProfile = lecturerProfileRepository.findByUserId(dto.getLecturerId())
            .orElseThrow(() -> new IllegalArgumentException("Giảng viên chưa có hồ sơ khoa/ngành"));

    if (!lecturerProfile.getDepartment().getId().equals(dto.getDepartmentId()))
      throw new IllegalArgumentException("Giảng viên không thuộc khoa/ngành đã chọn");

    if (sessionRepository.existsByLecturerIdAndStartTimeAndStatusNot(dto.getLecturerId(), dto.getStartTime(), SessionStatus.CANCELLED))
      throw new IllegalArgumentException("Giảng viên đã có lịch ở khung giờ này");

    if (sessionRepository.existsByStudentIdAndStartTimeAndStatusNot(studentId, dto.getStartTime(), SessionStatus.CANCELLED))
      throw new IllegalArgumentException("Bạn đã có lịch ở khung giờ này");

    User student = userRepository.findById(studentId).orElseThrow();

    User lecturer = userRepository.findById(dto.getLecturerId()).orElseThrow();

    MentoringSession s = MentoringSession.builder()
            .student(student)
            .lecturer(lecturer)
            .startTime(dto.getStartTime())
            .endTime(dto.getStartTime().plusHours(1))
            .status(SessionStatus.PENDING)
            .build();

    return sessionRepository.save(s);
  }

  public List<MentoringSession> mySessions(Long studentId) {
    return sessionRepository.findByStudentIdOrderByStartTimeDesc(studentId);
  }

  public Map<Long, BorrowStatus> borrowingStatuses(Long studentId) {
    Map<Long, BorrowStatus> statuses = new HashMap<>();
    for (MentoringSession session : mySessions(studentId)) {
      borrowingRecordRepository.findBySessionId(session.getId())
              .ifPresent(record -> statuses.put(session.getId(), record.getStatus()));
    }
    return statuses;
  }

  public List<MentoringSession> pendingForLecturer(Long lecturerId) {
    return sessionRepository.findByLecturerIdAndStatusOrderByStartTimeAsc(lecturerId, SessionStatus.PENDING);
  }

  public MentoringSession evaluableSessionForLecturer(Long sessionId, Long lecturerId) {
    MentoringSession s = sessionRepository.findById(sessionId).orElseThrow();
    if (!s.getLecturer().getId().equals(lecturerId))
      throw new IllegalArgumentException("Không có quyền xử lý lịch này");
    if (s.getStatus() != SessionStatus.PENDING)
      throw new IllegalArgumentException("Lịch không ở trạng thái chờ xử lý");
    if (LocalDateTime.now().isBefore(s.getEndTime()))
      throw new IllegalArgumentException("Chỉ được đánh giá sau khi buổi tư vấn kết thúc");
    return s;
  }

  @Transactional
  public void evaluate(Long lecturerId, EvaluationDto dto) {
    MentoringSession s = evaluableSessionForLecturer(dto.getSessionId(), lecturerId);

    s.setStatus(SessionStatus.COMPLETED);

    evaluationRepository.save(AcademicEvaluation.builder()
            .session(s)
            .competencyReview(dto.getCompetencyReview())
            .score(dto.getScore())
            .build());

    BorrowingRecord record = BorrowingRecord.builder()
            .session(s)
            .status(BorrowStatus.WAITING_ISSUE)
            .build();

    for (int i = 0; i < dto.getEquipmentIds().size(); i++) {
      Long eqId = dto.getEquipmentIds().get(i);
      Integer qty = dto.getQuantities().size() > i ? dto.getQuantities().get(i) : 1;

      if (eqId == null || qty == null || qty <= 0)
        continue;

      Equipment equipment = equipmentRepository.findById(eqId).orElseThrow();
      if (Boolean.TRUE.equals(equipment.getDeleted()))
        throw new IllegalArgumentException("Thiết bị đã bị xóa mềm: " + equipment.getName());

      BorrowingDetail detail = BorrowingDetail.builder()
              .record(record)
              .equipment(equipment)
              .quantity(qty)
              .build();

      record.getDetails().add(detail);
    }
    borrowingRecordRepository.save(record);
  }

  public List<AcademicHistoryDto> academicHistory(Long studentId) {
    return sessionRepository.completedHistory(studentId)
            .stream().map(historyMapper::toDto)
            .toList();
  }

  @Transactional
  public void cancel(Long sessionId, Long studentId) {
    MentoringSession s = sessionRepository.findById(sessionId).orElseThrow();

    if (!s.getStudent().getId().equals(studentId))
      throw new IllegalArgumentException("Không có quyền hủy lịch này");

    if (LocalDateTime.now().isAfter(s.getStartTime().minusHours(cancelBeforeHours)))
      throw new IllegalArgumentException("Đã quá giới hạn thời gian cho phép hủy");

    s.setStatus(SessionStatus.CANCELLED);
  }
}
