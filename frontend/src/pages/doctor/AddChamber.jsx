import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function AddChamber() {
  const [name, setName] = useState('')
  const [address, setAddress] = useState('')
  const [city, setCity] = useState('')
  const [phone, setPhone] = useState('')
  const [chambers, setChambers] = useState([])
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadChambers()
  }, [])

  const loadChambers = () => {
    api.get('/doctor/chambers').then(({ data }) => setChambers(data)).catch(() => setChambers([]))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setSuccess('')
    setError('')
    try {
      await api.post('/doctor/chambers', { name, address, city, phone })
      setSuccess('Chamber added successfully')
      setName('')
      setAddress('')
      setCity('')
      setPhone('')
      loadChambers()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add chamber')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card" style={{ maxWidth: 500 }}>
      <h2 className="section-title mb-4">Add Chamber</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Chamber Name</label>
          <input value={name} onChange={(e) => setName(e.target.value)} required placeholder="e.g. Dhanmondi Chamber" />
        </div>
        <div className="form-group">
          <label>Address</label>
          <input value={address} onChange={(e) => setAddress(e.target.value)} required placeholder="Full address" />
        </div>
        <div className="form-group">
          <label>City</label>
          <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="City" />
        </div>
        <div className="form-group">
          <label>Phone</label>
          <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Contact number" />
        </div>
        {error && <p className="error-msg">{error}</p>}
        {success && <p className="success-msg">{success}</p>}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Adding...' : 'Add Chamber'}
        </button>
      </form>

      {chambers.length > 0 && (
        <div className="mt-4">
          <h3 className="section-title">Your Chambers</h3>
          <ul style={{ listStyle: 'none' }}>
            {chambers.map((c) => (
              <li key={c.id} style={{ padding: '8px 0', borderBottom: '1px solid var(--color-slate-200)' }}>
                <strong>{c.name}</strong> - {c.address}, {c.city}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}