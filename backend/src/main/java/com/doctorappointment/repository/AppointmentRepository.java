package com.doctorappointment.repository;

import com.doctorappointment.entity.Appointment;
import com.doctorappointment.entity.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
}
