import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function MyAppointments() {
  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [payingId, setPayingId] = useState(null)
  const [bkashTxId, setBkashTxId] = useState('')
  const [payLoading, setPayLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    loadAppointments()
  }, [])

  const loadAppointments = () => {
    setLoading(true)
    api.get('/patient/appointments')
      .then(({ data }) => setAppointments(data))
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false))
  }

  const handlePay = async (appointment) => {
    if (!bkashTxId.trim()) {
      setError('Please enter a Bkash transaction ID')
      return
    }
    setError('')
    setPayLoading(true)
    try {
      await api.post('/patient/appointments/pay', {
        appointmentId: appointment.id,
        bkashTransactionId: bkashTxId.trim(),
        transactionId: bkashTxId.trim(),
        amount: appointment.paymentAmount,
        paymentAmount: appointment.paymentAmount
      })
      setSuccess('Payment successful! Appointment confirmed.')
      setBkashTxId('')
      setPayingId(null)
      loadAppointments()
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed. Try a different transaction ID.')
    } finally {
      setPayLoading(false)
    }
  }

  if (loading) return <p>Loading appointments...</p>

  if (appointments.length === 0) {
    return <div className="card"><p>No appointments yet. <a href="/patient/book">Book one now</a></p></div>
  }

  return (
    <div className="card">
      <h2 className="section-title">My Appointments</h2>

      {success && <p className="success-msg mb-4">{success}</p>}

      <table className="data-table">
        <thead>
          <tr>
            <th>Doctor</th>
            <th>Chamber</th>
            <th>Date</th>
            <th>Time</th>
            <th>Status</th>
            <th>Payment</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((a) => (
            <>
              <tr key={a.id}>
                <td>{a.doctorName}</td>
                <td>{a.chamberName || '-'}</td>
                <td>{a.appointmentDate}</td>
                <td>{a.appointmentTime}</td>
                <td><span className={`badge badge-${a.status?.toLowerCase()}`}>{a.status}</span></td>
                <td><span className={`badge badge-${a.paymentStatus === 'PAID' ? 'confirmed' : 'pending'}`}>{a.paymentStatus}</span></td>
                <td>
                  {a.paymentStatus !== 'PAID' && a.status === 'CONFIRMED' && (
                    <button
                      className="btn btn-primary"
                      style={{ padding: '4px 12px', fontSize: 13 }}
                      onClick={() => { setPayingId(payingId === a.id ? null : a.id); setError(''); setSuccess('') }}
                    >
                      {payingId === a.id ? 'Cancel' : 'Pay Now'}
                    </button>
                  )}
                </td>
              </tr>
              {payingId === a.id && (
                <tr key={`pay-${a.id}`}>
                  <td colSpan={7} style={{ padding: '16px', background: '#fafafa', borderBottom: '1px solid #e2e8f0' }}>
                    <div style={{ display: 'flex', gap: 24, flexWrap: 'wrap', alignItems: 'flex-start', marginBottom: 16 }}>
                      {/* QR Code */}
                      <div style={{ textAlign: 'center' }}>
                        <img
                          src="/phonepe-qr.jpeg"
                          alt="PhonePe QR"
                          style={{ width: 130, height: 'auto', borderRadius: 8, border: '2px solid #7c3aed' }}
                        />
                        <p style={{ fontSize: 12, fontWeight: 600, color: '#7c3aed', marginTop: 4 }}>PhonePe</p>
                        <p style={{ fontSize: 11, color: '#555' }}>Badhavath Ram Charan Tej</p>
                      </div>
                      {/* Instructions */}
                      <div style={{ flex: 1, minWidth: 180 }}>
                        <p style={{ fontWeight: 600, marginBottom: 8 }}>Amount due: <span style={{ color: '#16a34a' }}>₹{a.paymentAmount || 0}</span></p>
                        <ol style={{ paddingLeft: 16, fontSize: 13, lineHeight: 1.9, color: '#444' }}>
                          <li>Open PhonePe → Scan QR code</li>
                          <li>Pay <strong>₹{a.paymentAmount || 0}</strong></li>
                          <li>Copy Transaction ID from PhonePe</li>
                          <li>Paste below and confirm</li>
                        </ol>
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
                      <input
                        placeholder="Enter PhonePe Transaction ID"
                        value={bkashTxId}
                        onChange={(e) => { setBkashTxId(e.target.value); setError('') }}
                        style={{ flex: 1, minWidth: 220 }}
                      />
                      <button
                        className="btn btn-primary"
                        onClick={() => handlePay(a)}
                        disabled={payLoading}
                      >
                        {payLoading ? 'Processing...' : 'Confirm Payment'}
                      </button>
                    </div>
                    {error && <p className="error-msg" style={{ marginTop: 8 }}>{error}</p>}
                  </td>
                </tr>
              )}
            </>
          ))}
        </tbody>
      </table>
    </div>
  )
}