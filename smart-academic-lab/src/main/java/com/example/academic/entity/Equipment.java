package com.example.academic.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "equipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String unit;

  @Column(nullable = false)
  private Integer stockQuantity;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean deleted = false;
}
