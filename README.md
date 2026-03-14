# Doctor Appointment System

A full-stack doctor appointment and management system with React frontend, Spring Boot backend, and MySQL database. Based on the user flow: Login → Patient (Search Doctor, Select Chamber, Select Date/Time, Bkash Payment) | Doctor (Add Chamber, Add Schedule, View Patients) | Admin (Approve/Reject Doctors, Approve/Reject Appointments, Manage Users).

## Project Structure

```
doctor-appointment-system/
├── backend/          # Spring Boot API
├── frontend/         # React (Vite) SPA
├── database/         # MySQL schema
└── README.md
```

## Prerequisites

- **Java 17**
- **Node.js 18+**
- **MySQL 8**
- **Maven**

## Setup

### 1. Database

Create the database and run the schema:

```bash
mysql -u root -p < database/schema.sql
```

Or run the SQL file manually in MySQL Workbench / any MySQL client. Update `application.properties` if your MySQL credentials differ:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/doctor_appointment_db?...
spring.datasource.username=root
spring.datasource.password=your_password
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API runs at `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at `http://localhost:3000`

## Default Credentials

After running the schema, you can use:

| Role    | Username | Password  |
|---------|----------|-----------|
| Admin   | admin    | admin123  |

Create Patient or Doctor accounts via **Create new account** on the login page.

## API Overview

### Auth
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register (Patient/Doctor)

### Patient
- `GET /api/patient/doctors` - Search doctors
- `GET /api/patient/doctors/{id}/chambers` - Get chambers
- `GET /api/patient/doctors/{id}/slots?chamberId=&date=` - Available slots
- `POST /api/patient/appointments` - Book appointment
- `POST /api/patient/appointments/pay` - Bkash payment
- `GET /api/patient/appointments` - My appointments

### Doctor
- `GET /api/doctor/chambers` - My chambers
- `POST /api/doctor/chambers` - Add chamber
- `GET /api/doctor/schedules` - My schedules
- `POST /api/doctor/schedules` - Add schedule
- `GET /api/doctor/patients` - Patient appointments

### Admin
- `GET /api/admin/doctors/pending` - Pending doctors
- `POST /api/admin/doctors/{id}/approve` - Approve doctor
- `POST /api/admin/doctors/{id}/reject` - Reject doctor
- `GET /api/admin/appointments/pending` - Pending appointments
- `POST /api/admin/appointments/{id}/approve` - Approve appointment
- `POST /api/admin/appointments/{id}/reject` - Reject appointment
- `GET /api/admin/doctors` - All doctors
- `GET /api/admin/patients` - All patients

## Bkash Payment

Payment validation is simulated. Enter any unique Bkash transaction ID and the correct amount to confirm. In production, integrate with the Bkash API for real validation.
