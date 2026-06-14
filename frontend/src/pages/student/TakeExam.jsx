import { useEffect, useState, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

export default function TakeExam() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [exam, setExam] = useState(null)
  const [answers, setAnswers] = useState({})
  const [current, setCurrent] = useState(0)
  const [timeLeft, setTimeLeft] = useState(0)
  const [submitting, setSubmitting] = useState(false)
  const timerRef = useRef()

  useEffect(() => {
    api.get(`/exams/${id}`).then(r => {
      setExam(r.data)
      setTimeLeft(r.data.durationMinutes * 60)
    })
  }, [id])

  useEffect(() => {
    if (!timeLeft) return
    timerRef.current = setInterval(() => {
      setTimeLeft(t => { if (t <= 1) { clearInterval(timerRef.current); handleSubmit(); return 0 } return t - 1 })
    }, 1000)
    return () => clearInterval(timerRef.current)
  }, [timeLeft > 0])

  const handleSubmit = async () => {
    if (submitting) return
    setSubmitting(true)
    clearInterval(timerRef.current)
    try {
      const res = await api.post('/results/submit', { examId: parseInt(id), answers })
      navigate('/student/results', { state: { result: res.data } })
    } catch (err) {
      alert(err.response?.data || 'Submission failed')
      setSubmitting(false)
    }
  }

  if (!exam) return <div className="min-h-screen flex items-center justify-center">Loading...</div>

  const questions = exam.questions || []
  const q = questions[current]
  const mins = String(Math.floor(timeLeft / 60)).padStart(2, '0')
  const secs = String(timeLeft % 60).padStart(2, '0')
  const answered = Object.keys(answers).length

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-3xl mx-auto p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">{exam.title}</h2>
          <span className={`font-mono text-lg font-bold px-3 py-1 rounded ${timeLeft < 60 ? 'bg-red-100 text-red-600' : 'bg-blue-100 text-blue-700'}`}>{mins}:{secs}</span>
        </div>
        <div className="flex gap-1 flex-wrap mb-4">
          {questions.map((_, i) => (
            <button key={i} onClick={() => setCurrent(i)}
              className={`w-8 h-8 rounded text-xs font-bold ${answers[questions[i].id] ? 'bg-green-500 text-white' : i === current ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}>
              {i + 1}
            </button>
          ))}
        </div>
        {q && (
          <div className="bg-white rounded-xl shadow p-6">
            <p className="text-sm text-gray-500 mb-1">Question {current + 1} of {questions.length} • {q.marks} mark(s)</p>
            <p className="font-medium text-gray-800 mb-4">{q.questionText}</p>
            <div className="space-y-3">
              {['A','B','C','D'].map(opt => (
                <label key={opt} className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition ${answers[q.id] === opt ? 'bg-blue-50 border-blue-400' : 'hover:bg-gray-50 border-gray-200'}`}>
                  <input type="radio" name={`q${q.id}`} checked={answers[q.id] === opt} onChange={() => setAnswers({...answers, [q.id]: opt})} />
                  <span className="font-medium text-sm">{opt}.</span>
                  <span className="text-sm">{q[`option${opt}`]}</span>
                </label>
              ))}
            </div>
          </div>
        )}
        <div className="flex justify-between items-center mt-4">
          <button onClick={() => setCurrent(c => Math.max(0, c - 1))} disabled={current === 0} className="bg-gray-200 px-4 py-2 rounded disabled:opacity-40">← Prev</button>
          <span className="text-sm text-gray-500">{answered}/{questions.length} answered</span>
          {current < questions.length - 1
            ? <button onClick={() => setCurrent(c => c + 1)} className="bg-blue-600 text-white px-4 py-2 rounded">Next →</button>
            : <button onClick={handleSubmit} disabled={submitting} className="bg-green-600 text-white px-4 py-2 rounded disabled:opacity-60">
                {submitting ? 'Submitting...' : 'Submit Exam'}
              </button>
          }
        </div>
      </div>
    </div>
  )
}
