import { Routes, Route, NavLink } from 'react-router-dom'
import DoctorRequests from './DoctorRequests'
import AppointmentRequests from './AppointmentRequests'
import ManageUsers from './ManageUsers'

export default function AdminDashboard() {
  return (
    <div className="container">
      <h1 className="dashboard-title">Admin Dashboard</h1>
      <p className="dashboard-subtitle">Manage doctors, patients, and appointments</p>

      <nav className="tabs">
        <NavLink to="/admin" end className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Doctor Requests
        </NavLink>
        <NavLink to="/admin/appointments" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Appointment Requests
        </NavLink>
        <NavLink to="/admin/users" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Manage Users
        </NavLink>
      </nav>

      <Routes>
        <Route index element={<DoctorRequests />} />
        <Route path="appointments" element={<AppointmentRequests />} />
        <Route path="users" element={<ManageUsers />} />
      </Routes>
    </div>
  )
}
