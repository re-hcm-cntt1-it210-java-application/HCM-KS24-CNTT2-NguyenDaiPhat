package com.example.academic.repository;
import com.example.academic.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
public interface LecturerProfileRepository extends JpaRepository<LecturerProfile, Long> {
  List<LecturerProfile> findByDepartmentId(Long departmentId);
  Optional<LecturerProfile> findByUserId(Long userId);

  @Query("select lp from LecturerProfile lp join fetch lp.user join fetch lp.department order by lp.department.name, lp.user.fullName")
  List<LecturerProfile> findAllForSchedule();
}
