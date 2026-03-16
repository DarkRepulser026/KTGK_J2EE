package com.example.demo.repository;

import com.example.demo.model.Enrollment;
import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(Student student);

    @Query("select e from Enrollment e join fetch e.course c left join fetch c.category where e.student = :student order by e.enrollDate desc, e.id desc")
    List<Enrollment> findByStudentWithCourse(@Param("student") Student student);

    boolean existsByStudentStudentIdAndCourseId(Long studentId, Long courseId);
}
