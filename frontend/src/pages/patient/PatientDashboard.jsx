import { Routes, Route, NavLink } from 'react-router-dom'
import SearchDoctors from './SearchDoctors'
import BookAppointment from './BookAppointment'
import MyAppointments from './MyAppointments'

export default function PatientDashboard() {
  return (
    <div className="container">
      <h1 className="dashboard-title">Patient Dashboard</h1>
      <p className="dashboard-subtitle">Search doctors, book appointments, and manage your visits</p>

      <nav className="tabs">
        <NavLink to="/patient" end className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Search Doctors
        </NavLink>
        <NavLink to="/patient/book" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Book Appointment
        </NavLink>
        <NavLink to="/patient/appointments" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          My Appointments
        </NavLink>
      </nav>

      <Routes>
        <Route index element={<SearchDoctors />} />
        <Route path="book" element={<BookAppointment />} />
        <Route path="appointments" element={<MyAppointments />} />
      </Routes>
    </div>
  )
}
