package com.doctorappointment.service;

import com.doctorappointment.dto.AppointmentDto;
import com.doctorappointment.dto.BookAppointmentRequest;
import com.doctorappointment.dto.PaymentRequest;
import com.doctorappointment.entity.*;
import com.doctorappointment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ChamberRepository chamberRepository;
    private final TransactionRepository transactionRepository;
    private final TimeScheduleRepository scheduleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
                             DoctorRepository doctorRepository, ChamberRepository chamberRepository,
                             TransactionRepository transactionRepository, TimeScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.chamberRepository = chamberRepository;
        this.transactionRepository = transactionRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Appointment bookAppointment(Long patientId, BookAppointmentRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Chamber chamber = chamberRepository.findById(request.getChamberId())
                .orElseThrow(() -> new RuntimeException("Chamber not found"));

        if (!chamber.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Chamber does not belong to this doctor");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setChamber(chamber);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setPaymentStatus(Appointment.PaymentStatus.PENDING);
        appointment.setPaymentAmount(doctor.getConsultationFee());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setNotes(request.getNotes());
        appointment.setPaymentMethod("BKASH");

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment processPayment(Long patientId, PaymentRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (appointment.getPaymentStatus() == Appointment.PaymentStatus.PAID) {
            throw new RuntimeException("Appointment already paid");
        }

        // Validate Bkash transaction (simplified - in production, call Bkash API)
        if (transactionRepository.existsByBkashTransactionId(request.getBkashTransactionId())) {
            throw new RuntimeException("Invalid transaction - already used");
        }

        if (request.getAmount() < appointment.getPaymentAmount()) {
            throw new RuntimeException("Invalid transaction - amount mismatch");
        }

        Transaction transaction = new Transaction();
        transaction.setBkashTransactionId(request.getBkashTransactionId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transaction.setPatient(appointment.getPatient());
        transaction = transactionRepository.save(transaction);

        appointment.setTransaction(transaction);
        appointment.setPaymentStatus(Appointment.PaymentStatus.PAID);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentDto> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::toAppointmentDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::toAppointmentDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(Long doctorId, Long chamberId, LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        TimeSchedule.DayOfWeek scheduleDay = TimeSchedule.DayOfWeek.valueOf(dayOfWeek.name());
        List<TimeSchedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, scheduleDay).stream()
                .filter(s -> chamberId == null || s.getChamber() == null || (s.getChamber() != null && s.getChamber().getId().equals(chamberId)))
                .collect(Collectors.toList());

        List<LocalTime> slots = new ArrayList<>();
        for (TimeSchedule s : schedules) {
            if (s.getAvailable() == null || !s.getAvailable()) continue;
            int duration = (s.getSlotDurationMinutes() != null) ? s.getSlotDurationMinutes() : 30;
            LocalTime current = s.getStartTime();
            LocalTime end = s.getEndTime();
            while (current != null && !current.plusMinutes(duration).isAfter(end)) {
                slots.add(current);
                current = current.plusMinutes(duration);
            }
        }

        // Remove already booked slots
        List<Appointment> existing = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        List<LocalTime> booked = existing.stream()
                .filter(a -> a.getChamber() == null || (a.getChamber() != null && a.getChamber().getId().equals(chamberId)))
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toList());
        slots.removeAll(booked);

        return slots.stream().distinct().sorted().collect(Collectors.toList());
    }

    private AppointmentDto toAppointmentDto(Appointment a) {
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
