
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.AuthResponse;
import com.PeerNest.PeerNest.Dto.LoginRequest;
import com.PeerNest.PeerNest.Dto.RegisterRequest;
import com.PeerNest.PeerNest.Entity.PasswordResetToken;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.PasswordResetTokenRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Security.JwtService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.password-reset-url}")
    private String passwordResetUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final int TOKEN_EXPIRY_MINUTES = 15;

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        return "Registration successful";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    // FORGOT PASSWORD
    @Transactional
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        // Do not reveal whether an email is registered.
        if (user == null) {
            return "If the email is registered, a password reset link will be sent.";
        }

        resetTokenRepository.deleteByUser(user);

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiryDate(LocalDateTime.now()
                        .plusMinutes(TOKEN_EXPIRY_MINUTES))
                .build();

        resetTokenRepository.save(resetToken);

        String resetLink = passwordResetUrl + "?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject("PeerNest - Password Reset");
        message.setText(
                "Hello " + user.getName() + ",\n\n" +
                        "We received a request to reset your PeerNest password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        resetLink + "\n\n" +
                        "This link expires in 15 minutes and can only be used once.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nPeerNest Team"
        );

        mailSender.send(message);

        return "If the email is registered, a password reset link will be sent.";
    }

    // RESET PASSWORD
    @Transactional
    public String resetPassword(String rawToken, String newPassword) {

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken =
                resetTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            resetTokenRepository.delete(resetToken);

            throw new RuntimeException("Password reset link has expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Delete token so it cannot be reused.
        resetTokenRepository.delete(resetToken);

        return "Password reset successfully. Please login with your new password.";
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception exception) {
            throw new RuntimeException("Unable to process reset token");
        }
    }
}
