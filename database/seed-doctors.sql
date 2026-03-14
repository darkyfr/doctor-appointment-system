-- Run this to add sample doctors (if schema already applied)
-- Usage: mysql -u root -p doctor_appointment_db < seed-doctors.sql

USE doctor_appointment_db;

-- Sample doctors (password: admin123 for all)
INSERT IGNORE INTO users (username, password, email, full_name, phone, role) VALUES
('drsmith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drsmith@hospital.com', 'Dr. John Smith', '01711223344', 'DOCTOR'),
('drjones', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drjones@clinic.com', 'Dr. Sarah Jones', '01822334455', 'DOCTOR'),
('drrahman', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drrahman@clinic.com', 'Dr. Ayesha Rahman', '01933445566', 'DOCTOR'),
('drkarim', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drkarim@clinic.com', 'Dr. Mohammed Karim', '01644556677', 'DOCTOR'),
('drahmed', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drahmed@clinic.com', 'Dr. Fatima Ahmed', '01555667788', 'DOCTOR');

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Cardiology', 'MBBS, MD Cardiology', 15, 'Expert cardiologist.', 1500.00, 'APPROVED', 'BMDC-001' FROM users WHERE username = 'drsmith';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Neurology', 'MBBS, MD Neurology, DNB', 12, 'Brain and nervous system specialist.', 2000.00, 'APPROVED', 'BMDC-002' FROM users WHERE username = 'drjones';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Pediatrics', 'MBBS, DCH, MD Pediatrics', 10, 'Child health specialist.', 1200.00, 'APPROVED', 'BMDC-003' FROM users WHERE username = 'drrahman';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Orthopedics', 'MBBS, MS Ortho, FRCS', 18, 'Bone and joint specialist.', 1800.00, 'APPROVED', 'BMDC-004' FROM users WHERE username = 'drkarim';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Dermatology', 'MBBS, MD Dermatology', 8, 'Skin care specialist.', 1000.00, 'APPROVED', 'BMDC-005' FROM users WHERE username = 'drahmed';

-- Chambers
INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Chamber', '123 Medical St, Dhanmondi', 'Dhaka', '01711223344' FROM doctors WHERE license_number = 'BMDC-001';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Gulshan Chamber', '456 Health Avenue, Gulshan 2', 'Dhaka', '01822334455' FROM doctors WHERE license_number = 'BMDC-002';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Branch', '99 Green Road', 'Dhaka', '01822334456' FROM doctors WHERE license_number = 'BMDC-002';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Uttara Chamber', '78 Medical Tower, Sector 7', 'Dhaka', '01933445566' FROM doctors WHERE license_number = 'BMDC-003';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Banani Chamber', '22 Ortho Center, Road 12', 'Dhaka', '01644556677' FROM doctors WHERE license_number = 'BMDC-004';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Skin Care', '55 Lake Road, Dhanmondi', 'Dhaka', '01555667788' FROM doctors WHERE license_number = 'BMDC-005';

-- Schedules: Dr. Smith (BMDC-001) has slots EVERY day (chamber_id NULL = applies to all chambers)
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SUNDAY', '10:00:00', '14:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';

-- All doctors: add schedules for EVERY day (chamber_id NULL = applies to any chamber)
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-002';

INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-003';

INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-004';

INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-005';
