package com.doctorappointment.service;

import com.doctorappointment.dto.ChamberDto;
import com.doctorappointment.dto.DoctorAppointmentDto;
import com.doctorappointment.dto.DoctorProfileDto;
import com.doctorappointment.dto.DoctorSearchDto;
import com.doctorappointment.dto.TimeScheduleDto;
import com.doctorappointment.entity.*;
import com.doctorappointment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ChamberRepository chamberRepository;
    private final TimeScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(DoctorRepository doctorRepository, ChamberRepository chamberRepository,
                         TimeScheduleRepository scheduleRepository, AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.chamberRepository = chamberRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @Transactional
    public Doctor updateProfile(Long doctorId, DoctorProfileDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
        if (dto.getQualification() != null) doctor.setQualification(dto.getQualification());
        if (dto.getConsultationFee() != null) doctor.setConsultationFee(dto.getConsultationFee());
        if (dto.getBio() != null) doctor.setBio(dto.getBio());
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Chamber addChamber(Long doctorId, ChamberDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Chamber chamber = new Chamber();
        chamber.setDoctor(doctor);
        chamber.setName(dto.getName());
        chamber.setAddress(dto.getAddress());
        chamber.setCity(dto.getCity());
        chamber.setPhone(dto.getPhone());
        return chamberRepository.save(chamber);
    }

    public List<ChamberDto> getChambers(Long doctorId) {
        return chamberRepository.findByDoctorId(doctorId).stream()
                .map(this::toChamberDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeSchedule addTimeSchedule(Long doctorId, TimeScheduleDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        TimeSchedule schedule = new TimeSchedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setSlotDurationMinutes(dto.getSlotDurationMinutes() != null ? dto.getSlotDurationMinutes() : 30);
        if (dto.getChamberId() != null) {
            chamberRepository.findById(dto.getChamberId()).ifPresent(schedule::setChamber);
        }
        return scheduleRepository.save(schedule);
    }

    public List<TimeScheduleDto> getSchedules(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::toScheduleDto)
                .collect(Collectors.toList());
    }

    public List<DoctorAppointmentDto> getPatientAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::toDoctorAppointmentDto)
                .collect(Collectors.toList());
    }

    private DoctorAppointmentDto toDoctorAppointmentDto(Appointment a) {
        DoctorAppointmentDto dto = new DoctorAppointmentDto();
        dto.setId(a.getId());
        dto.setPatientName(a.getPatient().getUser().getFullName());
        dto.setChamberName(a.getChamber() != null ? a.getChamber().getName() : null);
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setStatus(a.getStatus().name());
        dto.setSymptoms(a.getSymptoms());
        return dto;
    }

    public List<DoctorSearchDto> searchDoctors(String specialization) {
        List<Doctor> doctors = specialization != null && !specialization.isBlank()
                ? doctorRepository.findBySpecializationContainingIgnoreCase(specialization)
                : doctorRepository.findAll();
        return doctors.stream()
                .filter(d -> d.getApprovalStatus() == Doctor.ApprovalStatus.APPROVED)
                .map(this::toDoctorSearchDto)
                .collect(Collectors.toList());
    }

    private ChamberDto toChamberDto(Chamber c) {
        ChamberDto dto = new ChamberDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setAddress(c.getAddress());
        dto.setCity(c.getCity());
        dto.setPhone(c.getPhone());
        return dto;
    }

    private TimeScheduleDto toScheduleDto(TimeSchedule s) {
        TimeScheduleDto dto = new TimeScheduleDto();
        dto.setId(s.getId());
        dto.setDayOfWeek(s.getDayOfWeek());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setSlotDurationMinutes(s.getSlotDurationMinutes());
        if (s.getChamber() != null) {
            dto.setChamberId(s.getChamber().getId());
        }
        return dto;
    }

    private DoctorSearchDto toDoctorSearchDto(Doctor d) {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setId(d.getId());
        dto.setFullName(d.getUser().getFullName());
        dto.setSpecialization(d.getSpecialization());
        dto.setQualification(d.getQualification());
        dto.setConsultationFee(d.getConsultationFee());
        dto.setChambers(getChambers(d.getId()));
        return dto;
    }
}
