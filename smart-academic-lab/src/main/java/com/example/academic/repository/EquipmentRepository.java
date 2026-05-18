package com.example.academic.repository;
import com.example.academic.entity.Equipment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from Equipment e where e.id = :id")
  Optional<Equipment> findByIdForUpdate(@Param("id") Long id);
}
