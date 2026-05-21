package com.example.academic.repository;
import com.example.academic.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DepartmentRepository extends JpaRepository<Department, Long> {
  Optional<Department> findByName(String name);
  List<Department> findAllByOrderByNameAsc();
}
