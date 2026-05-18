package com.example.academic.repository;
import com.example.academic.entity.AcademicEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AcademicEvaluationRepository extends JpaRepository<AcademicEvaluation, Long> {
  Optional<AcademicEvaluation> findBySessionId(Long sessionId);
}
