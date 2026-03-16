package com.example.demo.controller;

import com.example.demo.dto.RegisterForm;
import com.example.demo.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (StringUtils.hasText(registerForm.getUsername())
            && registrationService.usernameExists(registerForm.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "Username already exists");
        }
        if (StringUtils.hasText(registerForm.getEmail())
            && registrationService.emailExists(registerForm.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "Email already exists");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        registrationService.registerStudent(registerForm);
        model.addAttribute("registered", true);
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}
