# Expert Review Guide – Doctor Appointment System

A guide to help you explain the project and answer questions during an expert review.

---

## 1. Project Overview (30-second pitch)

> "This is a full-stack doctor appointment management system. Patients can search doctors, book appointments with Bkash payment, while doctors manage chambers and schedules. Admins approve new doctors and appointments. It uses React for the frontend, Spring Boot for the backend, and MySQL for the database, with JWT authentication."

---

## 2. Architecture & Tech Stack

| Layer      | Technology        | Rationale |
|------------|-------------------|-----------|
| Frontend   | React 18 + Vite   | Component-based UI, fast dev server |
| State      | React Context     | Global auth state without Redux |
| HTTP       | Axios             | Interceptors for auth, error handling |
| Backend    | Spring Boot 3.2   | Production-ready Java framework |
| Security   | Spring Security + JWT | Stateless, scalable auth |
| ORM        | Spring Data JPA   | Database abstraction |
| Database   | MySQL 8           | Relational data, ACID compliance |

**Why this stack?**
- **Separation of concerns**: Frontend (UI), backend (business logic, API), database (persistence).
- **JWT**: Stateless auth, suitable for SPAs; token can be validated without session storage.
- **Spring Boot**: Embedded server, auto-configuration, ecosystem for enterprise APIs.

---

## 3. Database Design

### Entity Relationship (Main Tables)

```
users ──┬── doctors (1:1)
        ├── patients (1:1)
        └── (admin via role)

doctors ── chambers (1:N)
doctors ── time_schedules (1:N)
doctors ── appointments (1:N)

patients ── appointments (1:N)

appointments ── transactions (1:1, optional)
```

### Design Decisions

| Decision | Reason |
|----------|--------|
| **Users + Doctors/Patients** | Single login, different roles; doctors and patients extend users. |
| **Chambers as separate table** | One doctor can have many chambers (locations). |
| **time_schedules with chamber_id nullable** | Schedules can apply to all chambers or one specific chamber. |
| **transactions table** | Stores Bkash transaction IDs for audit and duplicate prevention. |

---

## 4. Security

### Authentication flow
1. User logs in → backend validates credentials.
2. Backend returns JWT with user ID, username, role.
3. Frontend stores token in `localStorage` and sends `Authorization: Bearer <token>` on each request.
4. `JwtAuthFilter` validates token and sets `SecurityContext`.

### Security measures
- **BCrypt** for password hashing.
- **JWT** with HMAC-SHA256 (secret in config).
- **Role-based access**: `/api/patient/**` → PATIENT, `/api/doctor/**` → DOCTOR, `/api/admin/**` → ADMIN.
- **CORS** restricted to `http://localhost:3000`.

### Likely questions & answers

**Q: Why store JWT in localStorage?**  
A: Simpler for a demo. In production, consider `httpOnly` cookies to reduce XSS risk.

**Q: Why is CSRF disabled?**  
A: With JWT in headers (not cookies), CSRF is not an issue. Cookie-based auth would need CSRF protection.

**Q: How do you prevent duplicate payments?**  
A: `transactionRepository.existsByBkashTransactionId()` blocks reuse of the same transaction ID.

---

## 5. API Design

### REST conventions
- **GET** – read (doctors, chambers, slots, appointments).
- **POST** – create (register, book, pay, add chamber/schedule).
- **PUT** – update (e.g. doctor profile).

### Important endpoints

| Endpoint | Purpose |
|----------|---------|
| `GET /api/patient/doctors/{id}/slots?chamberId=&date=` | Available slots for doctor, chamber, date |
| `POST /api/patient/appointments` | Create appointment |
| `POST /api/patient/appointments/pay` | Confirm Bkash payment |

### Slot logic
1. Load schedules for doctor + day of week.
2. Filter by chamber (or include schedules with `chamber_id = NULL`).
3. Generate 30‑min slots between `start_time` and `end_time`.
4. Remove already booked slots.

---

## 6. Frontend Architecture

### Structure
- **Pages**: Login, Register, Patient/Doctor/Admin dashboards.
- **Context**: `AuthContext` for user and token.
- **Protected routes**: Redirect to login if not authenticated; role-based access to dashboards.

### State
- Local state with `useState` for forms and lists.
- `useEffect` for data loading on mount or when dependencies change.

---

## 7. Common Expert Questions & Answers

### Architecture

**Q: Why separate frontend and backend?**  
A: Clear separation of UI and business logic. Frontend and backend can be scaled or changed independently.

**Q: Why MySQL and not PostgreSQL/MongoDB?**  
A: MySQL fits well for relational data (users, doctors, chambers, appointments, transactions). Transactions and referential integrity are important here.

### Backend

**Q: Why JWT instead of session-based auth?**  
A: Stateless, works with SPAs and future mobile apps. No server-side session storage.

**Q: How do you handle concurrency when booking the same slot?**  
A: Right now there is no pessimistic/optimistic locking. In production, we’d add a unique constraint on `(doctor_id, appointment_date, appointment_time, chamber_id)` and handle conflicts in the API.

**Q: What about validation?**  
A: `@Valid` on controllers, `@NotNull`, `@NotBlank`, `@Email` on DTOs. Business rules (e.g. chamber belongs to doctor) are in services.

### Frontend

**Q: Why no Redux or similar?**  
A: For this size of app, Context + local state is enough. Redux would be considered for larger or more complex state.

**Q: How do you handle API errors?**  
A: Axios interceptor redirects on 401. Components use `.catch()` and show error messages.

### Business logic

**Q: How does Bkash integration work?**  
A: It’s simulated. We only check that the transaction ID is unique and the amount matches. Production would call Bkash APIs.

**Q: What if a doctor is approved but has no chambers/schedules?**  
A: They appear in search but have no bookable slots. We could add validation so approval requires at least one chamber and schedule.

---

## 8. Known Limitations

| Limitation | Mitigation for production |
|------------|----------------------------|
| No slot locking | Optimistic locking or DB unique constraints |
| JWT in localStorage | Consider `httpOnly` cookies |
| No rate limiting | Add Spring rate limiter or gateway |
| Bkash simulated | Integrate real Bkash API |
| No email/SMS | Add notifications for confirmations/reminders |
| Single-page frontend | Add server-side rendering if SEO matters |

---

## 9. Suggested Demo Flow

1. **Admin**: Log in as `admin` / `admin123` → show pending doctors → approve one.
2. **Doctor**: Log in as doctor → add chamber → add schedule.
3. **Patient**: Log in as patient → search doctors → pick doctor, chamber, date, time → book → enter Bkash ID → confirm.
4. **Doctor**: View patient appointments.

---

## 10. Quick Reference – File Locations

| Purpose | Location |
|---------|----------|
| Security config | `backend/.../config/SecurityConfig.java` |
| JWT handling | `backend/.../config/JwtUtil.java`, `JwtAuthFilter.java` |
| Slot logic | `backend/.../service/AppointmentService.java` → `getAvailableSlots()` |
| Auth flow | `backend/.../service/AuthService.java` |
| API routes | `backend/.../controller/*Controller.java` |
| Frontend auth | `frontend/src/context/AuthContext.jsx` |
| API client | `frontend/src/services/api.js` |

---

*Use this guide to prepare for the review and adjust answers to your actual implementation.*
