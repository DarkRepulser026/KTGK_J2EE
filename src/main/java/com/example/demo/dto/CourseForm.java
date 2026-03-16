package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForm {

    @NotBlank(message = "Course name is required")
    private String name;

    private String image;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must be at most 10")
    private Integer credits;

    @NotBlank(message = "Lecturer is required")
    private String lecturer;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
