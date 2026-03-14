import { useState, useEffect } from 'react'
import api from '../../services/api'

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

export default function AddSchedule() {
  const [chambers, setChambers] = useState([])
  const [dayOfWeek, setDayOfWeek] = useState('MONDAY')
  const [startTime, setStartTime] = useState('09:00')
  const [endTime, setEndTime] = useState('13:00')
  const [chamberId, setChamberId] = useState(null)
  const [schedules, setSchedules] = useState([])
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/doctor/chambers').then(({ data }) => setChambers(data)).catch(() => setChambers([]))
    loadSchedules()
  }, [])

  const loadSchedules = () => {
    api.get('/doctor/schedules').then(({ data }) => setSchedules(data)).catch(() => setSchedules([]))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setSuccess('')
    setError('')
    try {
      await api.post('/doctor/schedules', {
        dayOfWeek,
        startTime: startTime + ':00',
        endTime: endTime + ':00',
        slotDurationMinutes: 30,
        chamberId: chamberId || undefined
      })
      setSuccess('Schedule added successfully')
      loadSchedules()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add schedule')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card" style={{ maxWidth: 500 }}>
      <h2 className="section-title mb-4">Add Time Schedule</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Chamber (optional)</label>
          <select value={chamberId || ''} onChange={(e) => setChamberId(e.target.value ? Number(e.target.value) : null)}>
            <option value="">-- All chambers --</option>
            {chambers.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Day of Week</label>
          <select value={dayOfWeek} onChange={(e) => setDayOfWeek(e.target.value)}>
            {DAYS.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Start Time</label>
          <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>End Time</label>
          <input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} required />
        </div>
        {error && <p className="error-msg">{error}</p>}
        {success && <p className="success-msg">{success}</p>}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Adding...' : 'Add Schedule'}
        </button>
      </form>

      {schedules.length > 0 && (
        <div className="mt-4">
          <h3 className="section-title">Your Schedules</h3>
          <table className="data-table">
            <thead>
              <tr>
                <th>Day</th>
                <th>Time</th>
                <th>Chamber</th>
              </tr>
            </thead>
            <tbody>
              {schedules.map((s) => (
                <tr key={s.id}>
                  <td>{s.dayOfWeek}</td>
                  <td>{s.startTime} - {s.endTime}</td>
                  <td>{s.chamberId ? chambers.find(c => c.id === s.chamberId)?.name || '-' : 'All'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}