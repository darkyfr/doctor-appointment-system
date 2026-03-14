-- Doctor Appointment System Database Schema
-- Run this in MySQL to set up the database

CREATE DATABASE IF NOT EXISTS doctor_appointment_db;
USE doctor_appointment_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Doctors table (extends users)
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization VARCHAR(100),
    qualification VARCHAR(200),
    experience_years INT,
    bio TEXT,
    consultation_fee DOUBLE NOT NULL DEFAULT 0,
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    license_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Chambers table (doctors can have multiple chambers)
CREATE TABLE IF NOT EXISTS chambers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    address TEXT,
    city VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Time Schedules table (doctor availability)
CREATE TABLE IF NOT EXISTS time_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    chamber_id BIGINT,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL DEFAULT 30,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (chamber_id) REFERENCES chambers(id) ON DELETE SET NULL
);

-- Transactions table (Bkash payment records)
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bkash_transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount DOUBLE NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
    patient_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    chamber_id BIGINT,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    symptoms TEXT,
    notes TEXT,
    transaction_id BIGINT,
    payment_amount DOUBLE,
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) DEFAULT 'BKASH',
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (chamber_id) REFERENCES chambers(id) ON DELETE SET NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL
);

-- Default admin (password: admin123)
INSERT IGNORE INTO users (username, password, email, full_name, role)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@hospital.com', 'System Admin', 'ADMIN');

-- Sample doctor (password: admin123) - for testing
INSERT IGNORE INTO users (username, password, email, full_name, phone, role) VALUES
('drsmith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drsmith@hospital.com', 'Dr. John Smith', '01711223344', 'DOCTOR');

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, consultation_fee, approval_status, license_number)
SELECT id, 'Cardiology', 'MBBS, MD', 15, 1500.00, 'APPROVED', 'BMDC-001'
FROM users WHERE username = 'drsmith';

-- Sample chamber and schedule for testing
INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Chamber', '123 Medical St, Dhanmondi', 'Dhaka', '01711223344'
FROM doctors WHERE license_number = 'BMDC-001';

-- Dr. Smith: slots every day (chamber_id NULL = applies to any chamber)
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'MONDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'TUESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'WEDNESDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'THURSDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'FRIDAY', '09:00:00', '17:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SATURDAY', '09:00:00', '13:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';
INSERT IGNORE INTO time_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
SELECT id, 'SUNDAY', '10:00:00', '14:00:00', 30 FROM doctors WHERE license_number = 'BMDC-001';

-- More sample doctors (password: admin123 for all)
INSERT IGNORE INTO users (username, password, email, full_name, phone, role) VALUES
('drjones', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drjones@clinic.com', 'Dr. Sarah Jones', '01822334455', 'DOCTOR'),
('drrahman', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drrahman@clinic.com', 'Dr. Ayesha Rahman', '01933445566', 'DOCTOR'),
('drkarim', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drkarim@clinic.com', 'Dr. Mohammed Karim', '01644556677', 'DOCTOR'),
('drahmed', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'drahmed@clinic.com', 'Dr. Fatima Ahmed', '01555667788', 'DOCTOR');

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Neurology', 'MBBS, MD Neurology, DNB', 12, 'Specialized in brain and nervous system disorders.', 2000.00, 'APPROVED', 'BMDC-002'
FROM users WHERE username = 'drjones';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Pediatrics', 'MBBS, DCH, MD Pediatrics', 10, 'Expert in child health and development.', 1200.00, 'APPROVED', 'BMDC-003'
FROM users WHERE username = 'drrahman';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Orthopedics', 'MBBS, MS Ortho, FRCS', 18, 'Bone, joint and muscle specialist.', 1800.00, 'APPROVED', 'BMDC-004'
FROM users WHERE username = 'drkarim';

INSERT IGNORE INTO doctors (user_id, specialization, qualification, experience_years, bio, consultation_fee, approval_status, license_number)
SELECT id, 'Dermatology', 'MBBS, MD Dermatology', 8, 'Skin, hair and nail care specialist.', 1000.00, 'APPROVED', 'BMDC-005'
FROM users WHERE username = 'drahmed';

-- Chambers for new doctors
INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Gulshan Chamber', '456 Health Avenue, Gulshan 2', 'Dhaka', '01822334455'
FROM doctors WHERE license_number = 'BMDC-002';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Uttara Chamber', '78 Medical Tower, Sector 7', 'Dhaka', '01933445566'
FROM doctors WHERE license_number = 'BMDC-003';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Banani Chamber', '22 Ortho Center, Road 12', 'Dhaka', '01644556677'
FROM doctors WHERE license_number = 'BMDC-004';

INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Skin Care', '55 Lake Road, Dhanmondi', 'Dhaka', '01555667788'
FROM doctors WHERE license_number = 'BMDC-005';

-- Extra chamber for Dr. Jones
INSERT IGNORE INTO chambers (doctor_id, name, address, city, phone)
SELECT id, 'Dhanmondi Branch', '99 Green Road', 'Dhaka', '01822334456'
FROM doctors WHERE license_number = 'BMDC-002';

-- All doctors: slots every day
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
