package com.example.academic.entity;

import com.example.academic.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity
@Table(name = "borrowing_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowingRecord {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne(optional = false)
  private MentoringSession session;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BorrowStatus status;
  @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<BorrowingDetail> details = new ArrayList<>();
}
