package com.example.academic.repository;
import com.example.academic.entity.MentoringSession;
import com.example.academic.enums.SessionStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.*;
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {
  boolean existsByLecturerIdAndStartTimeAndStatusNot(Long lecturerId, LocalDateTime startTime, SessionStatus status);
  List<MentoringSession> findByStudentIdOrderByStartTimeDesc(Long studentId);
  List<MentoringSession> findByLecturerIdAndStatusOrderByStartTimeAsc(Long lecturerId, SessionStatus status);
  @Query("select s from MentoringSession s where s.student.id=:studentId and s.status='COMPLETED' order by s.startTime desc")
  List<MentoringSession> completedHistory(@Param("studentId") Long studentId);
}
