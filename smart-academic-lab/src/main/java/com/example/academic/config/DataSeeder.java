package com.example.academic.config;

import com.example.academic.entity.*;
import com.example.academic.enums.Role;
import com.example.academic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
  private final DepartmentRepository departments;
  private final LabRoomTypeRepository labRoomTypes;
  private final UserRepository users;
  private final LecturerProfileRepository lecturerProfiles;
  private final EquipmentRepository equipments;

  @Override
  public void run(String... args) {
    var enc = new BCryptPasswordEncoder();

    Department it = department("Công nghệ thông tin");
    Department electronics = department("Điện - Điện tử");
    department("Quản trị kinh doanh");

    labRoomType("Phòng IoT");
    labRoomType("Phòng phần mềm");
    labRoomType("Phòng điện tử cơ bản");

    User student = user("student", "123456", Role.STUDENT, "Nguyễn Văn Sinh Viên", "student@demo.local", enc);
    User lecturer = user("lecturer", "123456", Role.LECTURER, "ThS. Trần Giảng Viên", "lecturer@demo.local", enc);
    User lecturer2 = user("lecturer2", "123456", Role.LECTURER, "TS. Lê Cố Vấn", "lecturer2@demo.local", enc);
    user("admin", "123456", Role.ADMIN, "Quản trị viên", "admin@demo.local", enc);

    lecturerProfile(lecturer, it, "Java Web");
    lecturerProfile(lecturer2, electronics, "Hệ thống nhúng");

    equipment("Arduino Kit", "bộ", 10);
    equipment("Cảm biến nhiệt độ", "cái", 20);
    equipment("Tài liệu Spring MVC", "quyển", 5);
    equipment("Máy đo đa năng", "cái", 3);
  }

  private Department department(String name) {
    return departments.findByName(name)
            .orElseGet(() -> departments.save(Department.builder().name(name).build()));
  }

  private LabRoomType labRoomType(String name) {
    return labRoomTypes.findByName(name)
            .orElseGet(() -> labRoomTypes.save(LabRoomType.builder().name(name).build()));
  }

  private User user(String username, String rawPassword, Role role, String fullName, String email, BCryptPasswordEncoder enc) {
    return users.findByUsername(username).orElseGet(() -> users.save(User.builder()
            .username(username)
            .passwordHash(enc.encode(rawPassword))
            .role(role)
            .fullName(fullName)
            .email(email)
            .build()));
  }

  private void lecturerProfile(User user, Department department, String specialty) {
    lecturerProfiles.findByUserId(user.getId()).orElseGet(() -> lecturerProfiles.save(LecturerProfile.builder()
            .user(user)
            .department(department)
            .specialty(specialty)
            .build()));
  }

  private void equipment(String name, String unit, int stockQuantity) {
    boolean exists = equipments.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(name));
    if (!exists) {
      equipments.save(Equipment.builder()
              .name(name)
              .unit(unit)
              .stockQuantity(stockQuantity)
              .deleted(false)
              .build());
    }
  }
}
