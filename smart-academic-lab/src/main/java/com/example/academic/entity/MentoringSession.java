package com.example.academic.entity;

import com.example.academic.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentoring_sessions", indexes = @Index(name = "idx_lecturer_time", columnList = "lecturer_id,startTime,status"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentoringSession {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User student;

  @ManyToOne(optional = false)
  private User lecturer;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SessionStatus status;
}
