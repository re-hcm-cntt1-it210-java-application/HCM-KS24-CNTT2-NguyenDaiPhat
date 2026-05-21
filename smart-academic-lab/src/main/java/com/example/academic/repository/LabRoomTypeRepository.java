package com.example.academic.repository;

import com.example.academic.entity.LabRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface LabRoomTypeRepository extends JpaRepository<LabRoomType, Long> {
  Optional<LabRoomType> findByName(String name);
  List<LabRoomType> findAllByOrderByNameAsc();
}
