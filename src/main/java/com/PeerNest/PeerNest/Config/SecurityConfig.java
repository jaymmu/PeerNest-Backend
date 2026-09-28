package com.PeerNest.PeerNest.Config;

import com.PeerNest.PeerNest.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF because we are using JWT
                .csrf(csrf -> csrf.disable())

                // JWT authentication is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC APIs
                        // =========================

                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        .requestMatchers("/api/courses")
                        .permitAll()

                        .requestMatchers("/api/courses/search")
                        .permitAll()

                        .requestMatchers("/api/courses/*")
                        .permitAll()


                        // =========================
                        // STUDENT APIs
                        // =========================

                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")


                        // =========================
                        // INSTRUCTOR APIs
                        // =========================

                        .requestMatchers("/api/instructor/**")
                        .hasRole("INSTRUCTOR")


                        // =========================
                        // ADMIN APIs
                        // =========================

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")


                        // =========================
                        // OTHER REQUESTS
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}