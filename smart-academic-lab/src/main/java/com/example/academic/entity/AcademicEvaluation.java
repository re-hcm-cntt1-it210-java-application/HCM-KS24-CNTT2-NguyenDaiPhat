package com.example.academic.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "academic_evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AcademicEvaluation {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  private MentoringSession session;

  @Lob
  private String competencyReview;

  private Integer score;
}
