package com.example.academic.repository;
import com.example.academic.dto.TopLecturerStat;
import com.example.academic.entity.MentoringSession;
import com.example.academic.enums.SessionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {
  boolean existsByLecturerIdAndStartTimeAndStatusNot(Long lecturerId, LocalDateTime startTime, SessionStatus status);
  boolean existsByStudentIdAndStartTimeAndStatusNot(Long studentId, LocalDateTime startTime, SessionStatus status);
  List<MentoringSession> findByStudentIdOrderByStartTimeDesc(Long studentId);
  List<MentoringSession> findByLecturerIdAndStatusOrderByStartTimeAsc(Long lecturerId, SessionStatus status);
  @Query("select s from MentoringSession s where s.student.id=:studentId and s.status='COMPLETED' order by s.startTime desc")
  List<MentoringSession> completedHistory(@Param("studentId") Long studentId);

  @Query("""
      select l.id as lecturerId, l.fullName as lecturerName, count(s.id) as consultationCount
      from MentoringSession s
      join s.lecturer l
      where s.status = com.example.academic.enums.SessionStatus.COMPLETED
      group by l.id, l.fullName
      having count(s.id) > 0
      order by count(s.id) desc, l.fullName asc
      """)
  List<TopLecturerStat> topLecturersByCompletedConsultations(Pageable pageable);

  @Query(value = """
      select coalesce(max(t.consultation_count), 0)
      from (
        select count(*) as consultation_count
        from mentoring_sessions s
        where s.status = 'COMPLETED'
        group by s.lecturer_id
        having count(*) > 0
      ) t
      """, nativeQuery = true)
  Long maxCompletedConsultationCount();

  @Query("""
      select count(s.id)
      from MentoringSession s
      where s.status = com.example.academic.enums.SessionStatus.COMPLETED
      """)
  Long countCompletedSessions();
}
