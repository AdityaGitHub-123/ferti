package com.fertilizer.ruleengine.service;

import com.fertilizer.ruleengine.dto.AuthResponse;
import com.fertilizer.ruleengine.dto.ForgotPasswordRequest;
import com.fertilizer.ruleengine.dto.LoginRequest;
import com.fertilizer.ruleengine.dto.OtpVerifyRequest;
import com.fertilizer.ruleengine.dto.ResetPasswordRequest;
import com.fertilizer.ruleengine.dto.SignupRequest;
import com.fertilizer.ruleengine.entity.User;
import com.fertilizer.ruleengine.exception.ApiException;
import com.fertilizer.ruleengine.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class AuthService {
    private static final int OTP_EXPIRY_MINUTES = 10;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, PasswordResetSession> resetSessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new ApiException("Username or email already exists", HttpStatus.CONFLICT);
        }

        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthToken(token);
        user.setProfileImage("/images/ui/profile-placeholder.svg");
        User saved = userRepository.save(user);

        return toResponse(saved, token, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByIdentifier(request.getIdentifier().trim())
                .orElseThrow(() -> new ApiException("Invalid username/email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid username/email or password", HttpStatus.UNAUTHORIZED);
        }

        String token = UUID.randomUUID().toString();
        userRepository.updateToken(user.getId(), token);
        user.setAuthToken(token);
        return toResponse(user, token, "Login successful");
    }

    public void logout(String token) {
        User user = getUserFromToken(token);
        userRepository.updateToken(user.getId(), null);
    }

    public Map<String, String> startForgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("No account found with this registered email", HttpStatus.NOT_FOUND));

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        PasswordResetSession session = new PasswordResetSession(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES), null);
        resetSessions.put(email, session);

        System.out.println("Password reset OTP for " + email + ": " + otp);
        return Map.of(
                "message", "Verification code sent successfully",
                "email", email,
                "otp", otp
        );
    }

    public Map<String, String> verifyOtp(OtpVerifyRequest request) {
        String email = normalizeEmail(request.getEmail());
        PasswordResetSession session = validSession(email);

        if (!session.otp().equals(request.getOtp().trim())) {
            throw new ApiException("Invalid OTP. Please check the verification code and try again.", HttpStatus.BAD_REQUEST);
        }

        String resetToken = UUID.randomUUID().toString();
        resetSessions.put(email, new PasswordResetSession(session.otp(), session.expiresAt(), resetToken));
        return Map.of(
                "message", "OTP verified. You can now set a new password.",
                "email", email,
                "resetToken", resetToken
        );
    }

    public Map<String, String> resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        PasswordResetSession session = validSession(email);

        if (!StringUtils.hasText(session.resetToken()) || !session.resetToken().equals(request.getResetToken())) {
            throw new ApiException("Verify OTP before resetting your password", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("No account found with this registered email", HttpStatus.NOT_FOUND));
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));
        resetSessions.remove(email);

        return Map.of("message", "Password reset successful. Please login with your new password.");
    }

    public User getUserFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new ApiException("Authentication token is missing", HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByToken(token)
                .orElseThrow(() -> new ApiException("Session expired. Please login again.", HttpStatus.UNAUTHORIZED));
    }

    private AuthResponse toResponse(User user, String token, String message) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), token, message);
    }

    private PasswordResetSession validSession(String email) {
        PasswordResetSession session = resetSessions.get(email);
        if (session == null) {
            throw new ApiException("Generate an OTP first", HttpStatus.BAD_REQUEST);
        }
        if (LocalDateTime.now().isAfter(session.expiresAt())) {
            resetSessions.remove(email);
            throw new ApiException("OTP expired. Generate a new verification code.", HttpStatus.BAD_REQUEST);
        }
        return session;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private record PasswordResetSession(String otp, LocalDateTime expiresAt, String resetToken) {
    }
}
