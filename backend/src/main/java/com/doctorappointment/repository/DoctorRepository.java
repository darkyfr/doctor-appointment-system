package com.doctorappointment.repository;

import com.doctorappointment.entity.Doctor;
import com.doctorappointment.entity.Doctor.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByApprovalStatus(ApprovalStatus status);
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
