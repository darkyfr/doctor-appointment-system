package com.doctorappointment.controller;

import com.doctorappointment.dto.ChamberDto;
import com.doctorappointment.dto.DoctorSearchDto;
import com.doctorappointment.service.AppointmentService;
import com.doctorappointment.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public PublicController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
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
}
