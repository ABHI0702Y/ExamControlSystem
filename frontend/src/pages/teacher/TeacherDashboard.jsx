import { Link } from 'react-router-dom'
import Navbar from '../../components/Navbar'

export default function TeacherDashboard() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6">
        <h2 className="text-2xl font-bold mb-6">Teacher Dashboard</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Link to="/teacher/exams" className="bg-purple-500 text-white rounded-xl p-6 hover:opacity-90 shadow">
            <h3 className="text-lg font-bold mb-1">My Exams</h3>
            <p className="text-sm opacity-80">Create and manage your exams</p>
          </Link>
          <Link to="/teacher/results" className="bg-orange-500 text-white rounded-xl p-6 hover:opacity-90 shadow">
            <h3 className="text-lg font-bold mb-1">View Results</h3>
            <p className="text-sm opacity-80">See student performance</p>
          </Link>
        </div>
      </div>
    </div>
  )
}
