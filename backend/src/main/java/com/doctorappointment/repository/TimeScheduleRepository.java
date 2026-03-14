package com.doctorappointment.repository;

import com.doctorappointment.entity.TimeSchedule;
import com.doctorappointment.entity.TimeSchedule.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeScheduleRepository extends JpaRepository<TimeSchedule, Long> {
    List<TimeSchedule> findByDoctorId(Long doctorId);
    List<TimeSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
