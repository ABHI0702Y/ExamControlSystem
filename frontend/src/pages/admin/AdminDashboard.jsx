import { Link } from 'react-router-dom'
import Navbar from '../../components/Navbar'

export default function AdminDashboard() {
  const cards = [
    { label: 'Manage Users', desc: 'Add/edit/delete students and teachers', path: '/admin/users', color: 'bg-blue-500' },
    { label: 'Manage Questions', desc: 'View and manage question bank', path: '/admin/questions', color: 'bg-green-500' },
    { label: 'Manage Exams', desc: 'Create and configure exams', path: '/admin/exams', color: 'bg-purple-500' },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Admin Dashboard</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {cards.map(c => (
            <Link key={c.path} to={c.path}
              className={`${c.color} text-white rounded-xl p-6 hover:opacity-90 transition shadow`}>
              <h3 className="text-lg font-bold mb-1">{c.label}</h3>
              <p className="text-sm opacity-80">{c.desc}</p>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
