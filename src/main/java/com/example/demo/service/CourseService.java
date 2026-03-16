package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private static final int PAGE_SIZE = 5;

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final StudentRepository studentRepository;

    public Page<Course> getCoursesPage(int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
        return courseRepository.findAll(pageable);
    }

    public Page<Course> searchByName(String keyword, int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
        String safeKeyword = keyword == null ? "" : keyword.trim();
        return courseRepository.findByNameContainingIgnoreCase(safeKeyword, pageable);
    }

    public long countCourses() {
        return courseRepository.count();
    }

    public long countCategories() {
        return categoryRepository.count();
    }

    public long countStudents() {
        return studentRepository.count();
    }
}
