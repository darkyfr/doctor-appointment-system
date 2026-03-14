package com.doctorappointment.service;

import com.doctorappointment.entity.Appointment;
import com.doctorappointment.entity.Doctor;
import com.doctorappointment.entity.Patient;
import com.doctorappointment.entity.User;
import com.doctorappointment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminService(DoctorRepository doctorRepository, PatientRepository patientRepository,
                        UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Doctor> getPendingDoctors() {
        return doctorRepository.findByApprovalStatus(Doctor.ApprovalStatus.PENDING);
    }

    @Transactional
    public Doctor approveDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.setApprovalStatus(Doctor.ApprovalStatus.APPROVED);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor rejectDoctor(Long doctorId, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.setApprovalStatus(Doctor.ApprovalStatus.REJECTED);
        return doctorRepository.save(doctor);
    }

    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(Appointment.AppointmentStatus.PENDING);
    }

    @Transactional
    public Appointment approveAppointment(Long appointmentId) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        apt.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(apt);
    }

    @Transactional
    public Appointment rejectAppointment(Long appointmentId, String reason) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        apt.setStatus(Appointment.AppointmentStatus.REJECTED);
        apt.setRejectionReason(reason);
        return appointmentRepository.save(apt);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
