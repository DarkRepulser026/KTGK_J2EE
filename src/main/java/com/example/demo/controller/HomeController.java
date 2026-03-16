package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class HomeController {

    private final CourseService courseService;

    @GetMapping({"/", "/home", "/courses"})
    public String courses(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<Course> coursePage = courseService.getCoursesPage(page);
        mapCoursePageToModel(model, coursePage, page, null);
        return "index";
    }

    @GetMapping("/courses/search")
    public String searchCourses(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<Course> coursePage = courseService.searchByName(keyword, page);
        mapCoursePageToModel(model, coursePage, page, keyword);
        return "index";
    }

    private void mapCoursePageToModel(Model model, Page<Course> coursePage, int requestedPage, String keyword) {
        int safePage = Math.max(requestedPage, 0);
        int effectivePage = coursePage.getTotalPages() > 0 && safePage >= coursePage.getTotalPages()
                ? coursePage.getTotalPages() - 1
                : safePage;

        if (effectivePage != safePage) {
            coursePage = keyword == null
                    ? courseService.getCoursesPage(effectivePage)
                    : courseService.searchByName(keyword, effectivePage);
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", effectivePage);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        model.addAttribute("totalCourses", courseService.countCourses());
        model.addAttribute("totalStudents", courseService.countStudents());
        model.addAttribute("totalCategories", courseService.countCategories());
    }
}
