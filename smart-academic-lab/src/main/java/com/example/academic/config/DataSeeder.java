//package com.example.academic.config;
//import com.example.academic.entity.*;
//import com.example.academic.enums.Role;
//import com.example.academic.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//@Component @RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//  private final DepartmentRepository departments; private final UserRepository users; private final LecturerProfileRepository lecturerProfiles; private final EquipmentRepository equipments;
//  @Override public void run(String... args) {
//    var enc = new BCryptPasswordEncoder();
//    var it = departments.save(Department.builder().name("Cong nghe thong tin").build());
//    var ee = departments.save(Department.builder().name("Dien - Dien tu").build());
//    User student = users.save(User.builder().username("student").passwordHash(enc.encode("123456")).role(Role.STUDENT).fullName("Nguyen Van Sinh Vien").email("student@demo.local").build());
//    User lecturer = users.save(User.builder().username("lecturer").passwordHash(enc.encode("123456")).role(Role.LECTURER).fullName("ThS. Tran Giang Vien").email("lecturer@demo.local").build());
//    User admin = users.save(User.builder().username("admin").passwordHash(enc.encode("123456")).role(Role.ADMIN).fullName("Quan tri vien").email("admin@demo.local").build());
//    lecturerProfiles.save(LecturerProfile.builder().user(lecturer).department(it).specialty("Java Web").build());
//    equipments.save(Equipment.builder().name("Arduino Kit").unit("bo").stockQuantity(10).build());
//    equipments.save(Equipment.builder().name("Cam bien nhiet do").unit("cai").stockQuantity(20).build());
//    equipments.save(Equipment.builder().name("Tai lieu Spring MVC").unit("quyen").stockQuantity(5).build());
//  }
//}
