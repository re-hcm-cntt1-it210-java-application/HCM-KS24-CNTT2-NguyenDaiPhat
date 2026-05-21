package com.example.academic.repository;
import com.example.academic.entity.Equipment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
  List<Equipment> findAllByOrderByDeletedAscNameAsc();

  @Query("select e from Equipment e where e.deleted = false or e.deleted is null order by e.name")
  List<Equipment> findActiveOrderByNameAsc();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from Equipment e where e.id = :id")
  Optional<Equipment> findByIdForUpdate(@Param("id") Long id);
}
