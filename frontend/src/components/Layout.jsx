import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Layout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const getDashboardPath = () => {
    if (!user) return '/'
    switch (user.role) {
      case 'PATIENT': return '/patient'
      case 'DOCTOR': return '/doctor'
      case 'ADMIN': return '/admin'
      default: return '/'
    }
  }

  return (
    <div className="app-layout">
      <header className="header">
        <div className="container header-inner">
          <Link to={getDashboardPath()} className="logo">
            <span className="logo-icon">◇</span>
            DoctorApp
          </Link>
          <nav className="nav">
            <span className="user-info">{user?.fullName || user?.username} ({user?.role})</span>
            <button onClick={handleLogout} className="btn btn-ghost">Logout</button>
          </nav>
        </div>
      </header>
      <main className="main">
        {children}
      </main>
    </div>
  )
}
