package com.example.demo.config;

import com.example.demo.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/", "/home", "/courses", "/courses/**").permitAll()
            .requestMatchers("/register", "/login").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/enroll/**").hasRole("STUDENT")
            .anyRequest().authenticated()
                )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .permitAll()
        )
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .defaultSuccessUrl("/home", true)
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/home")
            .permitAll()
        );

        return http.build();
    }
}
