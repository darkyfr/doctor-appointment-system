package com.doctorappointment.controller;

import com.doctorappointment.dto.*;
import com.doctorappointment.service.AppointmentService;
import jakarta.validation.Valid;
import com.doctorappointment.service.DoctorService;
import com.doctorappointment.repository.PatientRepository;
import com.doctorappointment.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public PatientController(DoctorService doctorService, AppointmentService appointmentService,
                            UserRepository userRepository, PatientRepository patientRepository) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorSearchDto>> searchDoctors(
            @RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(doctorService.searchDoctors(specialization));
    }

    @GetMapping("/doctors/{doctorId}/chambers")
    public ResponseEntity<List<ChamberDto>> getChambers(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getChambers(doctorId));
    }

    @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<List<java.time.LocalTime>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam Long chamberId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, chamberId, localDate));
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BookAppointmentRequest request) {
        Long patientId = getPatientId(userDetails.getUsername());
        return ResponseEntity.ok(appointmentService.bookAppointment(patientId, request));
    }

    @PostMapping("/appointments/pay")
    public ResponseEntity<?> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest request) {
        Long patientId = getPatientId(userDetails.getUsername());
        return ResponseEntity.ok(appointmentService.processPayment(patientId, request));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long patientId = getPatientId(userDetails.getUsername());
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    private Long getPatientId(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return patient.getId();
    }
}
