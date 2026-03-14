import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function SearchDoctors() {
  const [doctors, setDoctors] = useState([])
  const [specialization, setSpecialization] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadDoctors()
  }, [specialization])

  const loadDoctors = async () => {
    setLoading(true)
    try {
      const params = specialization ? { specialization } : {}
      const { data } = await api.get('/patient/doctors', { params })
      setDoctors(data)
    } catch (err) {
      console.error(err)
      setDoctors([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="form-group mb-4" style={{ maxWidth: 400 }}>
        <label>Search by Specialization</label>
        <input
          type="text"
          placeholder="e.g. Cardiology, Neurology"
          value={specialization}
          onChange={(e) => setSpecialization(e.target.value)}
        />
      </div>

      {loading ? (
        <p>Loading doctors...</p>
      ) : doctors.length === 0 ? (
        <p className="card">No doctors found. Try a different search or ensure doctors are approved.</p>
      ) : (
        <div className="doctor-grid">
          {doctors.map((d) => (
            <div key={d.id} className="doctor-card card">
              <h3>{d.fullName}</h3>
              <p className="meta">{d.specialization || 'General'}</p>
              <p className="meta">{d.qualification}</p>
              <p className="fee">Fee: ৳{d.consultationFee || 0}</p>
              {d.chambers?.length > 0 && (
                <p className="meta" style={{ fontSize: 12 }}>
                  {d.chambers.length} chamber(s): {d.chambers.map(c => c.name).join(', ')}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
