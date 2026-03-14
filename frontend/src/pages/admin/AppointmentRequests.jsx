import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function AppointmentRequests() {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadAppointments()
  }, [])

  const loadAppointments = () => {
    api.get('/admin/appointments/pending')
      .then(({ data }) => setAppointments(data))
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false))
  }

  const handleApprove = async (id) => {
    try {
      await api.post(`/admin/appointments/${id}/approve`)
      loadAppointments()
    } catch (err) {
      alert(err.response?.data?.message || 'Failed')
    }
  }

  const handleReject = async (id) => {
    try {
      await api.post(`/admin/appointments/${id}/reject`)
      loadAppointments()
    } catch (err) {
      alert(err.response?.data?.message || 'Failed')
    }
  }

  if (loading) return <p>Loading...</p>

  if (appointments.length === 0) {
    return <div className="card"><p>No pending appointment requests.</p></div>
  }

  return (
    <div className="card">
      <h2 className="section-title">Accept/Reject Appointment Requests</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th>Patient</th>
            <th>Doctor</th>
            <th>Chamber</th>
            <th>Date</th>
            <th>Time</th>
            <th>Payment</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((a) => (
            <tr key={a.id}>
              <td>{a.patientName}</td>
              <td>{a.doctorName}</td>
              <td>{a.chamberName || '-'}</td>
              <td>{a.appointmentDate}</td>
              <td>{a.appointmentTime}</td>
              <td><span className={`badge badge-${a.paymentStatus === 'PAID' ? 'confirmed' : 'pending'}`}>{a.paymentStatus}</span></td>
              <td>
                <button className="btn btn-primary" style={{ marginRight: 8, padding: '6px 14px', fontSize: 13 }} onClick={() => handleApprove(a.id)}>
                  Approve
                </button>
                <button className="btn btn-ghost" style={{ padding: '6px 14px', fontSize: 13 }} onClick={() => handleReject(a.id)}>
                  Reject
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
