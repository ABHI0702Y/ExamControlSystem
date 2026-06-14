import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import AdminDashboard from './pages/admin/AdminDashboard'
import UserManagement from './pages/admin/UserManagement'
import QuestionManagement from './pages/admin/QuestionManagement'
import ExamManagement from './pages/admin/ExamManagement'
import TeacherDashboard from './pages/teacher/TeacherDashboard'
import ResultsView from './pages/teacher/ResultsView'
import StudentDashboard from './pages/student/StudentDashboard'
import TakeExam from './pages/student/TakeExam'
import MyResults from './pages/student/MyResults'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/admin" element={<ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/users" element={<ProtectedRoute roles={['ADMIN']}><UserManagement /></ProtectedRoute>} />
          <Route path="/admin/questions" element={<ProtectedRoute roles={['ADMIN']}><QuestionManagement /></ProtectedRoute>} />
          <Route path="/admin/exams" element={<ProtectedRoute roles={['ADMIN']}><ExamManagement /></ProtectedRoute>} />
          <Route path="/teacher" element={<ProtectedRoute roles={['TEACHER']}><TeacherDashboard /></ProtectedRoute>} />
          <Route path="/teacher/exams" element={<ProtectedRoute roles={['TEACHER']}><ExamManagement /></ProtectedRoute>} />
          <Route path="/teacher/results" element={<ProtectedRoute roles={['TEACHER']}><ResultsView /></ProtectedRoute>} />
          <Route path="/student" element={<ProtectedRoute roles={['STUDENT']}><StudentDashboard /></ProtectedRoute>} />
          <Route path="/student/exam/:id" element={<ProtectedRoute roles={['STUDENT']}><TakeExam /></ProtectedRoute>} />
          <Route path="/student/results" element={<ProtectedRoute roles={['STUDENT']}><MyResults /></ProtectedRoute>} />
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
