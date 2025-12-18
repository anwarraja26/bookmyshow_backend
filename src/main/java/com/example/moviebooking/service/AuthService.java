package com.example.moviebooking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.moviebooking.dto.AuthResponse;
import com.example.moviebooking.dto.LoginRequest;
import com.example.moviebooking.dto.SignupRequest;
import com.example.moviebooking.model.UserAccount;
import com.example.moviebooking.repository.UserAccountRepository;

@Service
public class AuthService {
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(SignupRequest request) {
        String userId = request.getUserId().trim();
        String name = request.getName().trim();
        String normalizedRole = request.getRole().trim().toUpperCase();

        if (!"ADMIN".equals(normalizedRole) && !"USER".equals(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be ADMIN or USER");
        }
        if (userRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User ID already registered");
        }
        UserAccount account = new UserAccount();
        account.setUserId(userId);
        account.setName(name);
        account.setRole(normalizedRole);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        UserAccount saved = userRepository.save(account);
        return new AuthResponse(saved.getUserId(), saved.getName(), saved.getRole());
    }

    public AuthResponse authenticate(LoginRequest request) {
        UserAccount account = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(account.getUserId(), account.getName(), account.getRole());
    }

    public UserAccount requireUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
