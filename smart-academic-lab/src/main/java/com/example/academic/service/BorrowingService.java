package com.example.academic.service;
import com.example.academic.entity.*;
import com.example.academic.enums.BorrowStatus;
import com.example.academic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @RequiredArgsConstructor
public class BorrowingService {
  private final BorrowingRecordRepository recordRepository;
  private final EquipmentRepository equipmentRepository;

  public List<BorrowingRecord> waitingRecords() {
    return recordRepository.findByStatus(BorrowStatus.WAITING_ISSUE);
  }

  @Transactional
  public void issue(Long recordId) {
    BorrowingRecord record = recordRepository.findById(recordId).orElseThrow();
    if (record.getStatus() != BorrowStatus.WAITING_ISSUE)
      throw new IllegalArgumentException("Phiếu mượn không ở trạng thái chờ cấp phát");

    for (BorrowingDetail d : record.getDetails()) {
      Equipment e = equipmentRepository.findByIdForUpdate(d.getEquipment().getId()).orElseThrow();

      if (Boolean.TRUE.equals(e.getDeleted()))
        throw new IllegalArgumentException("Thiết bị đã bị xóa mềm: " + e.getName());

      if (e.getStockQuantity() < d.getQuantity())
        throw new IllegalArgumentException("Thiếu tồn kho: " + e.getName());
    }
    for (BorrowingDetail d : record.getDetails()) {
      Equipment e = equipmentRepository.findByIdForUpdate(d.getEquipment().getId()).orElseThrow();
      e.setStockQuantity(e.getStockQuantity() - d.getQuantity());
    }
    record.setStatus(BorrowStatus.ISSUED);
  }
}
