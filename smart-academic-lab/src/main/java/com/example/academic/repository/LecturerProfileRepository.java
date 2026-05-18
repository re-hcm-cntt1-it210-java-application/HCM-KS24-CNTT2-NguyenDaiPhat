package com.example.academic.repository;
import com.example.academic.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LecturerProfileRepository extends JpaRepository<LecturerProfile, Long> {
  List<LecturerProfile> findByDepartmentId(Long departmentId);
  Optional<LecturerProfile> findByUserId(Long userId);
}
