package com.example.academic.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "borrowing_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowingDetail {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional = false)
  private BorrowingRecord record;
  @ManyToOne(optional = false)
  private Equipment equipment;
  @Column(nullable = false)
  private Integer quantity;
}
