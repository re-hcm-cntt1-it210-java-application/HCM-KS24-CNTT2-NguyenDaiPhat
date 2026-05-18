package com.example.academic.repository;
import com.example.academic.entity.BorrowingRecord;
import com.example.academic.enums.BorrowStatus;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
  Optional<BorrowingRecord> findBySessionId(Long sessionId);
  @EntityGraph(attributePaths = {"details", "details.equipment", "session", "session.student"})
  List<BorrowingRecord> findByStatus(BorrowStatus status);
}
