package com.example.demo.controller;

import com.example.demo.dto.CourseForm;
import com.example.demo.model.Category;
import com.example.demo.model.Course;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/course-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("courseForm", new CourseForm());
        prepareFormModel(model, "Create Course", "/admin/courses");
        return "admin/course-form";
    }

    @PostMapping
    public String createCourse(
            @Valid @ModelAttribute("courseForm") CourseForm courseForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model, "Create Course", "/admin/courses");
            return "admin/course-form";
        }

        Category category = categoryRepository.findById(courseForm.getCategoryId())
                .orElse(null);
        if (category == null) {
            bindingResult.rejectValue("categoryId", "invalid", "Selected category does not exist");
            prepareFormModel(model, "Create Course", "/admin/courses");
            return "admin/course-form";
        }

        Course course = Course.builder()
                .name(courseForm.getName().trim())
                .image(blankToNull(courseForm.getImage()))
                .credits(courseForm.getCredits())
                .lecturer(courseForm.getLecturer().trim())
                .category(category)
                .build();

        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course created successfully.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/admin/courses";
        }

        CourseForm courseForm = new CourseForm();
        courseForm.setName(course.getName());
        courseForm.setImage(course.getImage());
        courseForm.setCredits(course.getCredits());
        courseForm.setLecturer(course.getLecturer());
        courseForm.setCategoryId(course.getCategory() != null ? course.getCategory().getId() : null);

        model.addAttribute("courseForm", courseForm);
        prepareFormModel(model, "Update Course", "/admin/courses/" + id);
        return "admin/course-form";
    }

    @PostMapping("/{id}")
    public String updateCourse(
            @PathVariable Long id,
            @Valid @ModelAttribute("courseForm") CourseForm courseForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Course existing = courseRepository.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/admin/courses";
        }

        if (bindingResult.hasErrors()) {
            prepareFormModel(model, "Update Course", "/admin/courses/" + id);
            return "admin/course-form";
        }

        Category category = categoryRepository.findById(courseForm.getCategoryId()).orElse(null);
        if (category == null) {
            bindingResult.rejectValue("categoryId", "invalid", "Selected category does not exist");
            prepareFormModel(model, "Update Course", "/admin/courses/" + id);
            return "admin/course-form";
        }

        existing.setName(courseForm.getName().trim());
        existing.setImage(blankToNull(courseForm.getImage()));
        existing.setCredits(courseForm.getCredits());
        existing.setLecturer(courseForm.getLecturer().trim());
        existing.setCategory(category);
        courseRepository.save(existing);

        redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!courseRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/admin/courses";
        }

        courseRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        return "redirect:/admin/courses";
    }

    private void prepareFormModel(Model model, String title, String submitPath) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("formTitle", title);
        model.addAttribute("submitPath", submitPath);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
