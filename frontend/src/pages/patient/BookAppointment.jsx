import { useState, useEffect } from 'react'
import api from '../../services/api'

export default function BookAppointment() {
  const [doctors, setDoctors] = useState([])
  const [chambers, setChambers] = useState([])
  const [slots, setSlots] = useState([])
  const [selectedDoctor, setSelectedDoctor] = useState(null)
  const [selectedChamber, setSelectedChamber] = useState(null)
  const [selectedDate, setSelectedDate] = useState('')
  const [selectedSlot, setSelectedSlot] = useState(null)
  const [symptoms, setSymptoms] = useState('')
  const [bkashTxId, setBkashTxId] = useState('')
  const [appointment, setAppointment] = useState(null)
  const [loading, setLoading] = useState(false)
  const [slotsLoading, setSlotsLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => { loadDoctors() }, [])

  useEffect(() => {
    if (selectedDoctor) {
      api.get(`/patient/doctors/${selectedDoctor}/chambers`).then(({ data }) => setChambers(data))
    } else {
      setChambers([])
    }
    setSelectedChamber(null)
    setSlots([])
    setSelectedDate('')
    setSelectedSlot(null)
  }, [selectedDoctor])

  useEffect(() => {
    if (selectedDoctor && selectedChamber && selectedDate) {
      setSlotsLoading(true)
      api.get(`/patient/doctors/${selectedDoctor}/slots`, {
        params: { chamberId: selectedChamber, date: selectedDate }
      })
        .then(({ data }) => setSlots(Array.isArray(data) ? data : []))
        .catch(() => setSlots([]))
        .finally(() => setSlotsLoading(false))
    } else {
      setSlots([])
    }
    setSelectedSlot(null)
  }, [selectedDoctor, selectedChamber, selectedDate])

  const loadDoctors = async () => {
    try {
      const { data } = await api.get('/patient/doctors')
      setDoctors(data)
    } catch (err) {
      console.error(err)
    }
  }

  const handleBook = async () => {
    if (!selectedDoctor || !selectedChamber || !selectedDate || !selectedSlot) {
      setError('Please select doctor, chamber, date, and time')
      return
    }
    setError('')
    setLoading(true)
    try {
      const slotTime = typeof selectedSlot === 'string' ? selectedSlot : selectedSlot
      const response = await api.post('/patient/appointments', {
        doctorId: selectedDoctor,
        chamberId: selectedChamber,
        appointmentDate: selectedDate,
        appointmentTime: slotTime,
        symptoms
      })

      console.log('RAW response.data:', JSON.stringify(response.data))

      // Handle any response shape
      let appt = response.data
      if (appt && !appt.id && appt.appointment) appt = appt.appointment
      if (appt && !appt.id && appt.data) appt = appt.data

      // Fallback: fetch latest unpaid appointment
      if (!appt || !appt.id) {
        console.warn('No id in response, fetching latest appointment...')
        const { data: list } = await api.get('/patient/appointments')
        const pending = Array.isArray(list) ? list.filter(a => a.paymentStatus === 'PENDING') : []
        appt = pending[pending.length - 1] || null
        console.log('Fetched latest pending appt:', JSON.stringify(appt))
      }

      setSuccess('Appointment created. Complete payment below.')
      setAppointment(appt || { id: null, paymentAmount: 0 })
    } catch (err) {
      console.error('Book error:', err.response?.data)
      setError(err.response?.data?.message || 'Failed to book')
    } finally {
      setLoading(false)
    }
  }

  const handlePay = async () => {
    if (!appointment || !bkashTxId.trim()) {
      setError('Enter PhonePe transaction ID')
      return
    }
    setError('')
    setLoading(true)
    try {
      await api.post('/patient/appointments/pay', {
        appointmentId: appointment.id,
        bkashTransactionId: bkashTxId.trim(),
        transactionId: bkashTxId.trim(),
        amount: appointment.paymentAmount,
        paymentAmount: appointment.paymentAmount
      })
      setSuccess('Payment successful! Appointment confirmed.')
      setAppointment(null)
      setBkashTxId('')
      setSelectedDoctor(null)
      setSelectedChamber(null)
      setSelectedDate('')
      setSelectedSlot(null)
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed')
    } finally {
      setLoading(false)
    }
  }

  const today = new Date()
  const minDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`

  return (
    <div className="card" style={{ maxWidth: 600 }}>
      <h2 className="section-title mb-4">Book Appointment</h2>

      {!appointment ? (
        <>
          <div className="form-group">
            <label>Select Doctor</label>
            <select value={selectedDoctor || ''} onChange={(e) => setSelectedDoctor(Number(e.target.value) || null)}>
              <option value="">-- Choose Doctor --</option>
              {doctors.map((d) => (
                <option key={d.id} value={d.id}>{d.user?.fullName || d.fullName} - {d.specialization || 'General'}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Select Chamber</label>
            <select value={selectedChamber || ''} onChange={(e) => setSelectedChamber(Number(e.target.value) || null)}>
              <option value="">-- Choose Chamber --</option>
              {chambers.map((c) => (
                <option key={c.id} value={c.id}>{c.name} - {c.address}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Select Date</label>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              min={minDate}
            />
          </div>

          <div className="form-group">
            <label>Select Time</label>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {slotsLoading ? (
                <span style={{ color: '#6b7280', fontSize: 14 }}>Loading slots...</span>
              ) : (
                slots.map((s) => {
                  let slotStr = s
                  if (typeof s !== 'string') {
                    slotStr = (s?.hour != null) ? `${String(s.hour).padStart(2, '0')}:${String(s.minute || 0).padStart(2, '0')}` : String(s ?? '')
                  }
                  if (typeof slotStr === 'string' && slotStr.length > 5) slotStr = slotStr.substring(0, 5)
                  return (
                    <button
                      key={slotStr}
                      type="button"
                      className={`btn ${selectedSlot === s || selectedSlot === slotStr ? 'btn-primary' : 'btn-secondary'}`}
                      style={{ padding: '8px 16px', fontSize: 14 }}
                      onClick={() => setSelectedSlot(s)}
                    >
                      {slotStr}
                    </button>
                  )
                })
              )}
              {!slotsLoading && slots.length === 0 && selectedDate && (
                <span style={{ color: '#6b7280', fontSize: 14 }}>No slots available. Try another date.</span>
              )}
            </div>
          </div>

          <div className="form-group">
            <label>Symptoms (optional)</label>
            <textarea value={symptoms} onChange={(e) => setSymptoms(e.target.value)} placeholder="Describe your symptoms..." />
          </div>

          {error && <p className="error-msg">{error}</p>}
          {success && <p className="success-msg">{success}</p>}
          <button className="btn btn-primary" onClick={handleBook} disabled={loading}>
            {loading ? 'Booking...' : 'Book Appointment'}
          </button>
        </>
      ) : (
        <div>
          <p className="success-msg mb-4">Appointment booked! Please complete payment to confirm.</p>

          <div style={{
            border: '1px solid #bfdbfe',
            borderRadius: 12,
            padding: '20px',
            marginBottom: 20,
            background: '#eff6ff',
            display: 'flex',
            gap: 24,
            flexWrap: 'wrap',
            alignItems: 'flex-start'
          }}>
            <div style={{ textAlign: 'center', minWidth: 160 }}>
              <img
                src="/phonepe-qr.jpeg"
                alt="PhonePe QR Code"
                style={{ width: 160, height: 'auto', borderRadius: 8, border: '2px solid #7c3aed' }}
              />
              <p style={{ marginTop: 6, fontSize: 13, fontWeight: 600, color: '#7c3aed' }}>PhonePe</p>
              <p style={{ fontSize: 12, color: '#555' }}>Badhavath Ram Charan Tej</p>
            </div>

            <div style={{ flex: 1, minWidth: 200 }}>
              <h3 style={{ marginBottom: 12, fontSize: 16, color: '#1e3a5f' }}>How to Pay</h3>
              <ol style={{ paddingLeft: 18, lineHeight: 2, fontSize: 14, color: '#374151' }}>
                <li>Open <strong>PhonePe</strong> app on your phone</li>
                <li>Tap <strong>Scan QR</strong> and scan the code</li>
                <li>Enter amount: <strong style={{ color: '#16a34a', fontSize: 16 }}>Rs.{appointment.paymentAmount}</strong></li>
                <li>Complete the payment</li>
                <li>Copy the <strong>Transaction ID</strong> from PhonePe</li>
                <li>Paste it below and click <strong>Confirm Payment</strong></li>
              </ol>
            </div>
          </div>

          <div className="form-group">
            <label>PhonePe Transaction ID</label>
            <input
              placeholder="e.g. T2603131234567890"
              value={bkashTxId}
              onChange={(e) => setBkashTxId(e.target.value)}
            />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <button className="btn btn-primary" onClick={handlePay} disabled={loading}>
            {loading ? 'Processing...' : 'Confirm Payment'}
          </button>
          <button className="btn btn-ghost mt-4" style={{ marginLeft: 8 }} onClick={() => { setAppointment(null); setError(''); setSuccess('') }}>
            Cancel
          </button>
        </div>
      )}
    </div>
  )
}