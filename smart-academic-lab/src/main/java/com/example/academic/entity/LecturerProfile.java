package com.example.academic.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "lecturers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LecturerProfile {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne(optional = false)
  private User user;
  @ManyToOne(optional = false)
  private Department department;
  private String specialty;
}
