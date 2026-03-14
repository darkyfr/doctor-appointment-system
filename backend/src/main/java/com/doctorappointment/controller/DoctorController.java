package com.doctorappointment.controller;

import com.doctorappointment.dto.ChamberDto;
import com.doctorappointment.dto.DoctorAppointmentDto;
import com.doctorappointment.dto.DoctorProfileDto;
import com.doctorappointment.dto.TimeScheduleDto;
import com.doctorappointment.repository.UserRepository;
import com.doctorappointment.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final UserRepository userRepository;

    public DoctorController(DoctorService doctorService, UserRepository userRepository) {
        this.doctorService = doctorService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow().getId();
        return ResponseEntity.ok(doctorService.getDoctorByUserId(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DoctorProfileDto dto) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.updateProfile(doctorId, dto));
    }

    @PostMapping("/chambers")
    public ResponseEntity<?> addChamber(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChamberDto dto) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.addChamber(doctorId, dto));
    }

    @GetMapping("/chambers")
    public ResponseEntity<List<ChamberDto>> getChambers(@AuthenticationPrincipal UserDetails userDetails) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.getChambers(doctorId));
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> addTimeSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TimeScheduleDto dto) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.addTimeSchedule(doctorId, dto));
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<TimeScheduleDto>> getSchedules(@AuthenticationPrincipal UserDetails userDetails) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.getSchedules(doctorId));
    }

    @GetMapping("/patients")
    public ResponseEntity<List<DoctorAppointmentDto>> getPatients(@AuthenticationPrincipal UserDetails userDetails) {
        Long doctorId = doctorService.getDoctorByUserId(
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getId()).getId();
        return ResponseEntity.ok(doctorService.getPatientAppointments(doctorId));
    }
}
