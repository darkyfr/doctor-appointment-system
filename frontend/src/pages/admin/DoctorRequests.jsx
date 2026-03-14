import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function DoctorRequests() {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadDoctors()
  }, [])

  const loadDoctors = () => {
    api.get('/admin/doctors/pending')
      .then(({ data }) => setDoctors(data))
      .catch(() => setDoctors([]))
      .finally(() => setLoading(false))
  }

  const handleApprove = async (id) => {
    try {
      await api.post(`/admin/doctors/${id}/approve`)
      loadDoctors()
    } catch (err) {
      alert(err.response?.data?.message || 'Failed')
    }
  }

  const handleReject = async (id) => {
    try {
      await api.post(`/admin/doctors/${id}/reject`)
      loadDoctors()
    } catch (err) {
      alert(err.response?.data?.message || 'Failed')
    }
  }

  if (loading) return <p>Loading...</p>

  if (doctors.length === 0) {
    return <div className="card"><p>No pending doctor requests.</p></div>
  }

  return (
    <div className="card">
      <h2 className="section-title">Accept/Reject New Doctor Requests</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Specialization</th>
            <th>Qualification</th>
            <th>Fee</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {doctors.map((d) => (
            <tr key={d.id}>
              <td>{d.user?.fullName}</td>
              <td>{d.specialization || '-'}</td>
              <td>{d.qualification || '-'}</td>
              <td>৳{d.consultationFee || 0}</td>
              <td>
                <button className="btn btn-primary" style={{ marginRight: 8, padding: '6px 14px', fontSize: 13 }} onClick={() => handleApprove(d.id)}>
                  Approve
                </button>
                <button className="btn btn-ghost" style={{ padding: '6px 14px', fontSize: 13 }} onClick={() => handleReject(d.id)}>
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
