package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.model.Course;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            seedRoles();
            seedAdmin();
            seedCourses();
        };
    }

    private void seedRoles() {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().name("ADMIN").build());
        }
        if (roleRepository.findByName("STUDENT").isEmpty()) {
            roleRepository.save(Role.builder().name("STUDENT").build());
        }
    }

    private void seedCourses() {
        if (courseRepository.count() > 0) {
            return;
        }

        Category cs = categoryRepository.findByName("Computer Science")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Computer Science").build()));
        Category math = categoryRepository.findByName("Mathematics")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Mathematics").build()));
        Category business = categoryRepository.findByName("Business")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Business").build()));

        List<Course> courses = List.of(
                Course.builder().name("Introduction to Java Programming").credits(3).lecturer("Dr. Nguyen Van A").category(cs).image(null).build(),
                Course.builder().name("Data Structures and Algorithms").credits(3).lecturer("Dr. Le Van C").category(cs).image(null).build(),
                Course.builder().name("Database Systems").credits(3).lecturer("Prof. Tran Thi B").category(cs).image(null).build(),
                Course.builder().name("Operating Systems").credits(4).lecturer("Dr. Hoang Van D").category(cs).image(null).build(),
                Course.builder().name("Computer Networks").credits(3).lecturer("Dr. Pham Thi E").category(cs).image(null).build(),
                Course.builder().name("Linear Algebra").credits(4).lecturer("Prof. Nguyen Minh F").category(math).image(null).build(),
                Course.builder().name("Calculus II").credits(4).lecturer("Dr. Tran Quoc G").category(math).image(null).build(),
                Course.builder().name("Probability and Statistics").credits(3).lecturer("Dr. Le Huynh H").category(math).image(null).build(),
                Course.builder().name("Marketing Fundamentals").credits(3).lecturer("Ms. Vo Thi I").category(business).image(null).build(),
                Course.builder().name("Project Management").credits(2).lecturer("Mr. Doan Van K").category(business).image(null).build()
        );

        courseRepository.saveAll(courses);
    }

    private void seedAdmin() {
        if (studentRepository.existsByUsername("admin")) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not initialized"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        Student admin = Student.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@eduenroll.local")
                .roles(roles)
                .build();

        studentRepository.save(admin);
    }
}
