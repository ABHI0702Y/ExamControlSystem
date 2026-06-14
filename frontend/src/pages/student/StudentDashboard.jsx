import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

export default function StudentDashboard() {
  const [exams, setExams] = useState([])

  useEffect(() => { api.get('/exams').then(r => setExams(r.data)) }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold">Available Exams</h2>
          <Link to="/student/results" className="text-blue-600 hover:underline text-sm">My Results →</Link>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {exams.map(ex => (
            <div key={ex.id} className="bg-white rounded-xl shadow p-5">
              <h3 className="font-bold text-lg mb-1">{ex.title}</h3>
              <p className="text-gray-500 text-sm mb-3">{ex.description}</p>
              <div className="flex gap-2 text-xs mb-4">
                <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded">{ex.durationMinutes} mins</span>
                <span className="bg-gray-100 px-2 py-0.5 rounded">{ex.totalMarks} marks</span>
                <span className="bg-orange-100 text-orange-700 px-2 py-0.5 rounded">Pass: {ex.passingMarks}</span>
              </div>
              <Link to={`/student/exam/${ex.id}`} className="block text-center bg-blue-600 text-white py-2 rounded hover:bg-blue-700 text-sm font-medium">
                Start Exam
              </Link>
            </div>
          ))}
          {exams.length === 0 && <p className="text-gray-500 col-span-2 text-center py-12">No exams available</p>}
        </div>
      </div>
    </div>
  )
}
