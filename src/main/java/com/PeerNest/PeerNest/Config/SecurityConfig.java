package com.PeerNest.PeerNest.Config;

import com.PeerNest.PeerNest.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf -> csrf.disable())


                // ==========================================
                // SESSION MANAGEMENT
                // ==========================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth


                        // ----------------------------------
                        // AUTHENTICATION
                        // ----------------------------------
                        // Login, register, etc.

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // ----------------------------------
                        // PUBLIC COURSE APIs
                        // ----------------------------------
                        // Anyone can browse published courses.

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses/search"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses/*"
                        ).permitAll()


                        // ----------------------------------
                        // PAYMENT
                        // ----------------------------------
                        // Only logged-in students can purchase
                        // courses.

                        .requestMatchers(
                                "/api/payment/**"
                        ).hasRole("STUDENT")


                        // ----------------------------------
                        // STUDENT APIs
                        // ----------------------------------

                        .requestMatchers(
                                "/api/student/**"
                        ).hasRole("STUDENT")


                        // ----------------------------------
                        // INSTRUCTOR APIs
                        // ----------------------------------

                        .requestMatchers(
                                "/api/instructor/**"
                        ).hasRole("INSTRUCTOR")


                        // ----------------------------------
                        // ADMIN APIs
                        // ----------------------------------

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")


                        // ----------------------------------
                        // STATIC FRONTEND
                        // ----------------------------------

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/app.js",
                                "/style.css",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()


                        // ----------------------------------
                        // EVERYTHING ELSE
                        // ----------------------------------
                        // Any API not explicitly allowed
                        // requires authentication.

                        .anyRequest().authenticated()
                )


                // ==========================================
                // JWT FILTER
                // ==========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}