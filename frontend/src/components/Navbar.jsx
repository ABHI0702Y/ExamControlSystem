import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-blue-700 text-white px-6 py-3 flex justify-between items-center shadow">
      <h1 className="text-xl font-bold">Exam Control System</h1>
      <div className="flex items-center gap-4">
        <span className="text-sm">{user?.fullName} <span className="bg-blue-500 px-2 py-0.5 rounded text-xs">{user?.role}</span></span>
        <button onClick={handleLogout} className="bg-red-500 hover:bg-red-600 px-3 py-1 rounded text-sm">Logout</button>
      </div>
    </nav>
  )
}
