package com.example.demo.service;

import com.example.demo.dto.RegisterForm;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerStudent(RegisterForm form) {
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException("STUDENT role not initialized"));

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);

        Student student = Student.builder()
                .username(form.getUsername().trim())
                .password(passwordEncoder.encode(form.getPassword()))
                .email(form.getEmail().trim().toLowerCase())
                .roles(roles)
                .build();

        studentRepository.save(student);
    }

    public boolean usernameExists(String username) {
        return studentRepository.existsByUsername(username.trim());
    }

    public boolean emailExists(String email) {
        return studentRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional
    public Student findOrCreateGoogleStudent(String email, String displayName) {
        String normalizedEmail = email.trim().toLowerCase();

        return studentRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    Role studentRole = roleRepository.findByName("STUDENT")
                            .orElseThrow(() -> new IllegalStateException("STUDENT role not initialized"));

                    Set<Role> roles = new HashSet<>();
                    roles.add(studentRole);

                    Student student = Student.builder()
                            .username(generateUniqueUsername(normalizedEmail, displayName))
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .email(normalizedEmail)
                            .roles(roles)
                            .build();

                    return studentRepository.save(student);
                });
    }

    private String generateUniqueUsername(String email, String displayName) {
        String base = extractBaseUsername(email, displayName);
        String candidate = base;
        int suffix = 1;

        while (studentRepository.existsByUsername(candidate)) {
            candidate = base + suffix;
            suffix++;
        }

        return candidate;
    }

    private String extractBaseUsername(String email, String displayName) {
        String source = StringUtils.hasText(displayName) ? displayName : email;
        String normalized = source.toLowerCase()
                .replaceAll("[^a-z0-9]+", "")
                .trim();

        if (StringUtils.hasText(normalized)) {
            return normalized;
        }

        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex).replaceAll("[^a-z0-9]+", "");
        }

        return "student";
    }
}
