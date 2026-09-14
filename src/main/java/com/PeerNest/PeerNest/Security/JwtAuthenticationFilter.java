package com.PeerNest.PeerNest.Security;

import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        System.out.println("====================================");

        System.out.println(
                "Request: " +
                        request.getMethod() +
                        " " +
                        request.getRequestURI()
        );


        String authHeader =
                request.getHeader("Authorization");


        System.out.println(
                "Authorization Header: " +
                        authHeader
        );


        // ==========================================
        // NO JWT
        // ==========================================

        if (
                authHeader == null ||
                        !authHeader.startsWith("Bearer ")
        ) {

            System.out.println(
                    "No Bearer token found."
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // ==========================================
        // EXTRACT TOKEN
        // ==========================================

        String token =
                authHeader.substring(7);


        try {


            // ==========================================
            // VALIDATE JWT
            // ==========================================

            if (!jwtService.isTokenValid(token)) {

                System.out.println(
                        "JWT is INVALID."
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            System.out.println(
                    "JWT is VALID."
            );


            // ==========================================
            // GET EMAIL
            // ==========================================

            String email =
                    jwtService.extractEmail(token);


            System.out.println(
                    "Email from JWT: " +
                            email
            );


            // ==========================================
            // FIND USER
            // ==========================================

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);


            if (user == null) {

                System.out.println(
                        "User not found."
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            // ==========================================
            // ROLE
            // ==========================================

            String role =
                    user.getRole().name();


            System.out.println(
                    "User role: " +
                            role
            );


            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );


            // ==========================================
            // CREATE AUTHENTICATION
            // ==========================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            List.of(authority)
                    );


            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );


            System.out.println(
                    "SecurityContext authentication set."
            );


        } catch (Exception e) {

            System.out.println(
                    "JWT processing error: " +
                            e.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }


        // ==========================================
        // CONTINUE
        // ==========================================

        filterChain.doFilter(
                request,
                response
        );
    }
}