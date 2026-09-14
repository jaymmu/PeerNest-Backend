package com.PeerNest.PeerNest.Config;

import com.PeerNest.PeerNest.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf -> csrf.disable())


                // ==========================================
                // SESSION
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

                        // -------------------------------
                        // Temporary frontend
                        // -------------------------------

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/app.js",
                                "/style.css",
                                "/favicon.ico",
                                "/error"
                        )
                        .permitAll()


                        // -------------------------------
                        // Authentication
                        // -------------------------------

                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()


                        // -------------------------------
                        // Public courses
                        // -------------------------------

                        .requestMatchers(
                                "/api/courses/**"
                        )
                        .permitAll()


                        // -------------------------------
                        // Instructor APIs
                        // -------------------------------

                        .requestMatchers(
                                "/api/instructor/**"
                        )
                        .hasRole("INSTRUCTOR")


                        // -------------------------------
                        // Student APIs
                        // -------------------------------

                        .requestMatchers(
                                "/api/student/**"
                        )
                        .hasRole("STUDENT")


                        // -------------------------------
                        // Admin APIs
                        // -------------------------------

                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasRole("ADMIN")


                        // -------------------------------
                        // Everything else
                        // -------------------------------

                        .anyRequest()
                        .permitAll()
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