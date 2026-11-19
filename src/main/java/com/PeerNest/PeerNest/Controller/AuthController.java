
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.AuthResponse;
import com.PeerNest.PeerNest.Dto.LoginRequest;
import com.PeerNest.PeerNest.Dto.RegisterRequest;
import com.PeerNest.PeerNest.Dto.ForgotPasswordRequest;
import com.PeerNest.PeerNest.Dto.ResetPasswordRequest;
import com.PeerNest.PeerNest.Service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(
                authService.forgotPassword(request.getEmail())
        );
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(
                authService.resetPassword(
                        request.getToken(),
                        request.getNewPassword()
                )
        );
    }
}
