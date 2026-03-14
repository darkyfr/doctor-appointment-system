import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function ViewPatients() {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/doctor/patients')
      .then(({ data }) => setAppointments(data))
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>Loading...</p>

  if (appointments.length === 0) {
    return <div className="card"><p>No patient appointments yet.</p></div>
  }

  return (
    <div className="card">
      <h2 className="section-title">Patient Appointments</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th>Patient</th>
            <th>Chamber</th>
            <th>Date</th>
            <th>Time</th>
            <th>Status</th>
            <th>Symptoms</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((a) => (
            <tr key={a.id}>
              <td>{a.patientName || '-'}</td>
              <td>{a.chamberName || '-'}</td>
              <td>{a.appointmentDate}</td>
              <td>{a.appointmentTime}</td>
              <td><span className={`badge badge-${a.status?.toLowerCase()}`}>{a.status}</span></td>
              <td>{a.symptoms || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
