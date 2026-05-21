package com.example.academic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_room_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LabRoomType {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;
}
