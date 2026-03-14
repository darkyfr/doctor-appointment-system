import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function ManageUsers() {
  const [doctors, setDoctors] = useState([])
  const [patients, setPatients] = useState([])
  const [activeTab, setActiveTab] = useState('doctors')

  useEffect(() => {
    api.get('/admin/doctors').then(({ data }) => setDoctors(data)).catch(() => setDoctors([]))
    api.get('/admin/patients').then(({ data }) => setPatients(data)).catch(() => setPatients([]))
  }, [])

  return (
    <div className="card">
      <h2 className="section-title">Manage Doctor and Patient</h2>
      <div className="tabs mb-4">
        <button className={`tab ${activeTab === 'doctors' ? 'active' : ''}`} onClick={() => setActiveTab('doctors')}>
          Doctors
        </button>
        <button className={`tab ${activeTab === 'patients' ? 'active' : ''}`} onClick={() => setActiveTab('patients')}>
          Patients
        </button>
      </div>

      {activeTab === 'doctors' && (
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Specialization</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {doctors.map((d) => (
              <tr key={d.id}>
                <td>{d.user?.fullName}</td>
                <td>{d.specialization || '-'}</td>
                <td><span className={`badge badge-${d.approvalStatus?.toLowerCase()}`}>{d.approvalStatus}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {activeTab === 'patients' && (
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>City</th>
            </tr>
          </thead>
          <tbody>
            {patients.map((p) => (
              <tr key={p.id}>
                <td>{p.user?.fullName}</td>
                <td>{p.city || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
