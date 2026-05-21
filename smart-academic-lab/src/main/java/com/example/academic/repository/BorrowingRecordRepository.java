package com.example.academic.repository;
import com.example.academic.entity.BorrowingRecord;
import com.example.academic.enums.BorrowStatus;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
  Optional<BorrowingRecord> findBySessionId(Long sessionId);
  @EntityGraph(attributePaths = {"details", "details.equipment", "session", "session.student", "session.lecturer"})
  List<BorrowingRecord> findByStatus(BorrowStatus status);

  @Query("""
      select coalesce(sum(d.quantity), 0)
      from BorrowingRecord r
      join r.details d
      where r.status = com.example.academic.enums.BorrowStatus.ISSUED
      """)
  Long totalIssuedEquipmentQuantity();

  @Query("""
      select coalesce(sum(d.quantity), 0)
      from BorrowingRecord r
      join r.details d
      where r.status = com.example.academic.enums.BorrowStatus.WAITING_ISSUE
      """)
  Long totalWaitingIssueEquipmentQuantity();

  @Query("""
      select count(r.id)
      from BorrowingRecord r
      where r.status = com.example.academic.enums.BorrowStatus.WAITING_ISSUE
      """)
  Long countWaitingIssueRecords();
}
