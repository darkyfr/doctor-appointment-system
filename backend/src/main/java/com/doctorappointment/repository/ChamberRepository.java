package com.doctorappointment.repository;

import com.doctorappointment.entity.Chamber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamberRepository extends JpaRepository<Chamber, Long> {
    List<Chamber> findByDoctorId(Long doctorId);
}
