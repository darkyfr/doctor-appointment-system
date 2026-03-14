import { Routes, Route, NavLink } from 'react-router-dom'
import AddChamber from './AddChamber'
import AddSchedule from './AddSchedule'
import ViewPatients from './ViewPatients'

export default function DoctorDashboard() {
  return (
    <div className="container">
      <h1 className="dashboard-title">Doctor Dashboard</h1>
      <p className="dashboard-subtitle">Manage your chambers, schedules, and view patients</p>

      <nav className="tabs">
        <NavLink to="/doctor" end className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Add Chamber
        </NavLink>
        <NavLink to="/doctor/schedule" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          Add Time Schedule
        </NavLink>
        <NavLink to="/doctor/patients" className={({ isActive }) => `tab ${isActive ? 'active' : ''}`}>
          View Patients
        </NavLink>
      </nav>

      <Routes>
        <Route index element={<AddChamber />} />
        <Route path="schedule" element={<AddSchedule />} />
        <Route path="patients" element={<ViewPatients />} />
      </Routes>
    </div>
  )
}
