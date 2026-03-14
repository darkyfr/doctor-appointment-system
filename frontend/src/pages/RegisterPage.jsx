import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../services/api'

export default function Register() {
  const navigate = useNavigate()
  const [role, setRole] = useState('PATIENT')
  const [fullName, setFullName] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [city, setCity] = useState('')
  // Doctor-only fields
  const [specialization, setSpecialization] = useState('')
  const [qualification, setQualification] = useState('')
  const [consultationFee, setConsultationFee] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = { fullName, username, password, role, city }
      if (role === 'DOCTOR') {
        payload.specialization = specialization
        payload.qualification = qualification
        payload.consultationFee = consultationFee ? Number(consultationFee) : null
      }
      const { data } = await api.post('/auth/register', payload)
      localStorage.setItem('token', data.token)
      localStorage.setItem('role', data.role)
      if (data.role === 'DOCTOR') navigate('/doctor')
      else navigate('/patient')
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Try a different username.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16 }}>
      <div className="card" style={{ width: '100%', maxWidth: 480 }}>
        <h2 className="section-title mb-4">Create Account</h2>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>I am a</label>
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="PATIENT">Patient</option>
              <option value="DOCTOR">Doctor</option>
            </select>
          </div>

          <div className="form-group">
            <label>Full Name</label>
            <input
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              placeholder="Your full name"
            />
          </div>

          <div className="form-group">
            <label>Username</label>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="Choose a username"
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Choose a password"
            />
          </div>

          <div className="form-group">
            <label>City</label>
            <input
              value={city}
              onChange={(e) => setCity(e.target.value)}
              placeholder="Your city"
            />
          </div>

          {role === 'DOCTOR' && (
            <>
              <div className="form-group">
                <label>Specialization</label>
                <input
                  value={specialization}
                  onChange={(e) => setSpecialization(e.target.value)}
                  required
                  placeholder="e.g. Cardiology, Neurology"
                />
              </div>

              <div className="form-group">
                <label>Qualification</label>
                <input
                  value={qualification}
                  onChange={(e) => setQualification(e.target.value)}
                  required
                  placeholder="e.g. MBBS, MD"
                />
              </div>

              <div className="form-group">
                <label>Consultation Fee (৳)</label>
                <input
                  type="number"
                  min="0"
                  value={consultationFee}
                  onChange={(e) => setConsultationFee(e.target.value)}
                  required
                  placeholder="e.g. 500"
                />
              </div>

              <p className="meta" style={{ marginBottom: 12 }}>
                Your account will be reviewed and approved by the admin before you can see patients.
              </p>
            </>
          )}

          {error && <p className="error-msg">{error}</p>}

          <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={loading}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="meta" style={{ marginTop: 16, textAlign: 'center' }}>
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  )
}
