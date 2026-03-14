package com.doctorappointment.config;

import com.doctorappointment.entity.*;
import com.doctorappointment.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * Ensures sample doctors and schedules exist on startup so slots are always available.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ChamberRepository chamberRepository;
    private final TimeScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DoctorRepository doctorRepository,
                          ChamberRepository chamberRepository, TimeScheduleRepository scheduleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.chamberRepository = chamberRepository;
        this.scheduleRepository = scheduleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (scheduleRepository.count() == 0 && doctorRepository.count() == 0) {
            // Fresh DB: create admin and sample doctors with schedules
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@hospital.com");
                admin.setFullName("System Admin");
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
            }
            ensureDoctorWithSchedules("drsmith", "Dr. John Smith", "Cardiology", "BMDC-001", 1500.0);
            ensureDoctorWithSchedules("drjones", "Dr. Sarah Jones", "Neurology", "BMDC-002", 2000.0);
            ensureDoctorWithSchedules("drrahman", "Dr. Ayesha Rahman", "Pediatrics", "BMDC-003", 1200.0);
            ensureDoctorWithSchedules("drkarim", "Dr. Mohammed Karim", "Orthopedics", "BMDC-004", 1800.0);
            ensureDoctorWithSchedules("drahmed", "Dr. Fatima Ahmed", "Dermatology", "BMDC-005", 1000.0);
        } else {
            // Add schedules for any doctor that has none (chamber_id = null so applies to all chambers)
            for (Doctor doctor : doctorRepository.findAll()) {
                if (scheduleRepository.findByDoctorId(doctor.getId()).isEmpty()) {
                    addSchedulesForDoctor(doctor);
                }
            }
        }
    }

    private void ensureDoctorWithSchedules(String username, String fullName, String specialization, String license, double fee) {
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setEmail(username + "@clinic.com");
            u.setFullName(fullName);
            u.setRole(User.Role.DOCTOR);
            return userRepository.save(u);
        });

        Doctor doctor = doctorRepository.findByUserId(user.getId()).orElseGet(() -> {
            Doctor d = new Doctor();
            d.setUser(user);
            d.setSpecialization(specialization);
            d.setConsultationFee(fee);
            d.setApprovalStatus(Doctor.ApprovalStatus.APPROVED);
            d.setLicenseNumber(license);
            return doctorRepository.save(d);
        });

        if (chamberRepository.findByDoctorId(doctor.getId()).isEmpty()) {
            Chamber chamber = new Chamber();
            chamber.setDoctor(doctor);
            chamber.setName("Main Chamber");
            chamber.setAddress("123 Medical St");
            chamber.setCity("Dhaka");
            chamberRepository.save(chamber);
        }

        if (scheduleRepository.findByDoctorId(doctor.getId()).isEmpty()) {
            addSchedulesForDoctor(doctor);
        }
    }

    private void addSchedulesForDoctor(Doctor doctor) {
        List<TimeSchedule.DayOfWeek> days = List.of(
            TimeSchedule.DayOfWeek.MONDAY, TimeSchedule.DayOfWeek.TUESDAY, TimeSchedule.DayOfWeek.WEDNESDAY,
            TimeSchedule.DayOfWeek.THURSDAY, TimeSchedule.DayOfWeek.FRIDAY, TimeSchedule.DayOfWeek.SATURDAY
        );
        for (TimeSchedule.DayOfWeek day : days) {
            TimeSchedule s = new TimeSchedule();
            s.setDoctor(doctor);
            s.setDayOfWeek(day);
            s.setStartTime(LocalTime.of(9, 0));
            s.setEndTime(LocalTime.of(17, 0));
            s.setSlotDurationMinutes(30);
            scheduleRepository.save(s);
        }
    }
}
