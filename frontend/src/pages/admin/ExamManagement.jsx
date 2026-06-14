import { useEffect, useState } from 'react'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

const EMPTY = { title: '', description: '', durationMinutes: 30, passingMarks: 0, active: true, startTime: '', endTime: '', questionIds: [] }

export default function ExamManagement() {
  const [exams, setExams] = useState([])
  const [questions, setQuestions] = useState([])
  const [form, setForm] = useState(EMPTY)
  const [editing, setEditing] = useState(null)

  const load = () => { api.get('/exams').then(r => setExams(r.data)); api.get('/questions').then(r => setQuestions(r.data)) }
  useEffect(() => { load() }, [])

  const toggleQ = id => setForm(f => ({ ...f, questionIds: f.questionIds.includes(id) ? f.questionIds.filter(x => x !== id) : [...f.questionIds, id] }))

  const save = async e => {
    e.preventDefault()
    const payload = { ...form, startTime: form.startTime || null, endTime: form.endTime || null }
    if (editing) await api.put(`/exams/${editing}`, payload)
    else await api.post('/exams', payload)
    setForm(EMPTY); setEditing(null); load()
  }

  const edit = ex => {
    setEditing(ex.id)
    setForm({ title: ex.title, description: ex.description || '', durationMinutes: ex.durationMinutes, passingMarks: ex.passingMarks, active: ex.active, startTime: ex.startTime?.slice(0,16) || '', endTime: ex.endTime?.slice(0,16) || '', questionIds: (ex.questions || []).map(q => q.id) })
  }

  const del = async id => { if (confirm('Delete exam?')) { await api.delete(`/exams/${id}`); load() } }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto p-6">
        <h2 className="text-2xl font-bold mb-6">Exam Management</h2>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-xl shadow p-6">
            <h3 className="font-bold text-lg mb-4">{editing ? 'Edit Exam' : 'Create Exam'}</h3>
            <form onSubmit={save} className="space-y-3">
              <input placeholder="Exam Title" required className="input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} />
              <textarea placeholder="Description" rows={2} className="input resize-none" value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
              <div className="grid grid-cols-2 gap-2">
                <input type="number" placeholder="Duration (mins)" required className="input" value={form.durationMinutes} onChange={e => setForm({...form, durationMinutes: +e.target.value})} />
                <input type="number" placeholder="Passing Marks" className="input" value={form.passingMarks} onChange={e => setForm({...form, passingMarks: +e.target.value})} />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div><label className="text-xs text-gray-500">Start Time</label><input type="datetime-local" className="input" value={form.startTime} onChange={e => setForm({...form, startTime: e.target.value})} /></div>
                <div><label className="text-xs text-gray-500">End Time</label><input type="datetime-local" className="input" value={form.endTime} onChange={e => setForm({...form, endTime: e.target.value})} /></div>
              </div>
              <label className="flex items-center gap-2 text-sm"><input type="checkbox" checked={form.active} onChange={e => setForm({...form, active: e.target.checked})} /> Active</label>
              <div>
                <p className="text-sm font-medium mb-2">Select Questions ({form.questionIds.length} selected)</p>
                <div className="max-h-48 overflow-y-auto border rounded p-2 space-y-1">
                  {questions.map(q => (
                    <label key={q.id} className="flex items-center gap-2 text-xs cursor-pointer hover:bg-gray-50 p-1 rounded">
                      <input type="checkbox" checked={form.questionIds.includes(q.id)} onChange={() => toggleQ(q.id)} />
                      <span className="flex-1 truncate">{q.questionText}</span>
                      <span className="text-gray-400 shrink-0">{q.subject}</span>
                    </label>
                  ))}
                </div>
              </div>
              <div className="flex gap-2">
                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded flex-1">{editing ? 'Update' : 'Create'}</button>
                {editing && <button type="button" onClick={() => {setEditing(null); setForm(EMPTY)}} className="bg-gray-200 px-3 py-2 rounded">Cancel</button>}
              </div>
            </form>
          </div>
          <div className="space-y-4">
            {exams.map(ex => (
              <div key={ex.id} className="bg-white rounded-xl shadow p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h4 className="font-bold">{ex.title}</h4>
                    <p className="text-sm text-gray-500">{ex.description}</p>
                    <div className="flex gap-2 mt-2 text-xs">
                      <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded">{ex.durationMinutes} mins</span>
                      <span className="bg-gray-100 px-2 py-0.5 rounded">{ex.totalMarks} marks</span>
                      <span className={`px-2 py-0.5 rounded ${ex.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>{ex.active ? 'Active' : 'Inactive'}</span>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button onClick={() => edit(ex)} className="text-blue-600 text-xs hover:underline">Edit</button>
                    <button onClick={() => del(ex.id)} className="text-red-500 text-xs hover:underline">Delete</button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
