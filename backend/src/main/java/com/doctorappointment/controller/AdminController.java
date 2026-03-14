package com.doctorappointment.controller;

import com.doctorappointment.dto.AppointmentDto;
import com.doctorappointment.entity.Appointment;
import com.doctorappointment.entity.Doctor;
import com.doctorappointment.entity.Patient;
import com.doctorappointment.service.AdminService;
import com.doctorappointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AppointmentService appointmentService;

    public AdminController(AdminService adminService, AppointmentService appointmentService) {
        this.adminService = adminService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/doctors/pending")
    public ResponseEntity<List<Doctor>> getPendingDoctors() {
        return ResponseEntity.ok(adminService.getPendingDoctors());
    }

    @PostMapping("/doctors/{id}/approve")
    public ResponseEntity<Doctor> approveDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveDoctor(id));
    }

    @PostMapping("/doctors/{id}/reject")
    public ResponseEntity<Doctor> rejectDoctor(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(adminService.rejectDoctor(id, reason));
    }

    @GetMapping("/appointments/pending")
    public ResponseEntity<List<AppointmentDto>> getPendingAppointments() {
        List<Appointment> appointments = adminService.getPendingAppointments();
        return ResponseEntity.ok(appointments.stream()
                .map(a -> toDto(a))
                .collect(Collectors.toList()));
    }

    @PostMapping("/appointments/{id}/approve")
    public ResponseEntity<Appointment> approveAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveAppointment(id));
    }

    @PostMapping("/appointments/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(adminService.rejectAppointment(id, reason));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    private AppointmentDto toDto(Appointment a) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(a.getId());
        dto.setPatientId(a.getPatient().getId());
        dto.setPatientName(a.getPatient().getUser().getFullName());
        dto.setDoctorId(a.getDoctor().getId());
        dto.setDoctorName(a.getDoctor().getUser().getFullName());
        dto.setChamberName(a.getChamber() != null ? a.getChamber().getName() : null);
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setStatus(a.getStatus());
        dto.setPaymentStatus(a.getPaymentStatus());
        dto.setPaymentAmount(a.getPaymentAmount());
        dto.setSymptoms(a.getSymptoms());
        return dto;
    }
}
