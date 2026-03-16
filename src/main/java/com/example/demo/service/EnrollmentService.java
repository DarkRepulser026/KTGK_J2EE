package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Enrollment;
import com.example.demo.model.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public boolean enrollCourse(String username, Long courseId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentStudentIdAndCourseId(
                student.getStudentId(),
                course.getId()
        );
        if (alreadyEnrolled) {
            return false;
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        enrollmentRepository.save(enrollment);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getMyEnrollments(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return enrollmentRepository.findByStudentWithCourse(student);
    }
}
