package com.example.academic.repository;

import com.example.academic.entity.User;
import com.example.academic.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  List<User> findByRole(Role role);
  boolean existsByUsername(String username);
}
