package com.doctorappointment.controller;

import com.doctorappointment.dto.AuthResponse;
import com.doctorappointment.dto.LoginRequest;
import com.doctorappointment.dto.RegisterRequest;
import com.doctorappointment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @GetMapping("/hash")
public String hash(@RequestParam String password) {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password);
}
}
