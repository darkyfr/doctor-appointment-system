package com.doctorappointment.service;

import com.doctorappointment.dto.AuthResponse;
import com.doctorappointment.dto.LoginRequest;
import com.doctorappointment.dto.RegisterRequest;
import com.doctorappointment.entity.*;
import com.doctorappointment.repository.*;
import com.doctorappointment.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository, DoctorRepository doctorRepository,
                       PatientRepository patientRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());

        Long profileId = null;
        if (user.getRole() == User.Role.DOCTOR) {
            profileId = doctorRepository.findByUserId(user.getId()).map(Doctor::getId).orElse(null);
        } else if (user.getRole() == User.Role.PATIENT) {
            profileId = patientRepository.findByUserId(user.getId()).map(Patient::getId).orElse(null);
        }

        String token = jwtUtil.generateToken(userDetails, claims);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), user.getRole(), profileId);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        // Optional fields — only set if present (avoids NPE if your User entity has these columns)
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        user = userRepository.save(user);

        if (request.getRole() == User.Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setApprovalStatus(Doctor.ApprovalStatus.PENDING);
            // FIX: persist doctor profile fields submitted during registration
            doctor.setSpecialization(request.getSpecialization());
            doctor.setQualification(request.getQualification());
            doctor.setConsultationFee(request.getConsultationFee());
            doctorRepository.save(doctor);
        } else if (request.getRole() == User.Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(user);
            if (request.getCity() != null) patient.setCity(request.getCity());
            patientRepository.save(patient);
        }

        return login(new LoginRequest(request.getUsername(), request.getPassword()));
    }
}