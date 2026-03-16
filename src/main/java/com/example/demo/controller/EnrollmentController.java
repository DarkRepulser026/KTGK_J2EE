package com.example.demo.controller;

import com.example.demo.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enroll")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public String enrollCourse(
            @PathVariable Long courseId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean enrolled = enrollmentService.enrollCourse(authentication.getName(), courseId);
            if (enrolled) {
                redirectAttributes.addFlashAttribute("successMessage", "Enroll course successfully.");
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "You already enrolled in this course.");
            }
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Authentication authentication, Model model) {
        model.addAttribute("enrollments", enrollmentService.getMyEnrollments(authentication.getName()));
        return "enroll/my-courses";
    }
}
