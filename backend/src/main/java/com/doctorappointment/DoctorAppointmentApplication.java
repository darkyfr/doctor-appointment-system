package com.doctorappointment;

import com.doctorappointment.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DoctorAppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorAppointmentApplication.class, args);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner resetAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").ifPresent(user -> {
                user.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(user);
                System.out.println("✅ Admin password reset to admin123");
            });
        };
    }
}