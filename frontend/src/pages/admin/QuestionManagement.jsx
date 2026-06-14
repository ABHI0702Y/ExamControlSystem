import { useEffect, useState } from 'react'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

const EMPTY = { questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctOption: 'A', marks: 1, difficulty: 'MEDIUM', subject: '' }

export default function QuestionManagement() {
  const [questions, setQuestions] = useState([])
  const [form, setForm] = useState(EMPTY)
  const [editing, setEditing] = useState(null)
  const [search, setSearch] = useState('')

  const load = () => api.get('/questions').then(r => setQuestions(r.data))
  useEffect(() => { load() }, [])

  const save = async e => {
    e.preventDefault()
    if (editing) await api.put(`/questions/${editing}`, form)
    else await api.post('/questions', form)
    setForm(EMPTY); setEditing(null); load()
  }

  const edit = q => { setEditing(q.id); setForm({ questionText: q.questionText, optionA: q.optionA, optionB: q.optionB, optionC: q.optionC, optionD: q.optionD, correctOption: q.correctOption, marks: q.marks, difficulty: q.difficulty, subject: q.subject || '' }) }
  const del = async id => { if (confirm('Delete?')) { await api.delete(`/questions/${id}`); load() } }

  const filtered = questions.filter(q => q.questionText.toLowerCase().includes(search.toLowerCase()) || (q.subject||'').toLowerCase().includes(search.toLowerCase()))

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto p-6">
        <h2 className="text-2xl font-bold mb-6">Question Management ({questions.length} questions)</h2>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="bg-white rounded-xl shadow p-6">
            <h3 className="font-bold text-lg mb-4">{editing ? 'Edit Question' : 'Add Question'}</h3>
            <form onSubmit={save} className="space-y-3">
              <textarea placeholder="Question Text" required rows={3} className="input resize-none" value={form.questionText} onChange={e => setForm({...form, questionText: e.target.value})} />
              {['A','B','C','D'].map(opt => <input key={opt} placeholder={`Option ${opt}`} required className="input" value={form[`option${opt}`]} onChange={e => setForm({...form, [`option${opt}`]: e.target.value})} />)}
              <select className="input" value={form.correctOption} onChange={e => setForm({...form, correctOption: e.target.value})}>
                {['A','B','C','D'].map(o => <option key={o}>{o}</option>)}
              </select>
              <div className="grid grid-cols-2 gap-2">
                <input type="number" min={1} placeholder="Marks" className="input" value={form.marks} onChange={e => setForm({...form, marks: +e.target.value})} />
                <select className="input" value={form.difficulty} onChange={e => setForm({...form, difficulty: e.target.value})}>
                  <option>EASY</option><option>MEDIUM</option><option>HARD</option>
                </select>
              </div>
              <input placeholder="Subject" className="input" value={form.subject} onChange={e => setForm({...form, subject: e.target.value})} />
              <div className="flex gap-2">
                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded flex-1">{editing ? 'Update' : 'Add'}</button>
                {editing && <button type="button" onClick={() => {setEditing(null); setForm(EMPTY)}} className="bg-gray-200 px-3 py-2 rounded">Cancel</button>}
              </div>
            </form>
          </div>
          <div className="lg:col-span-2">
            <input placeholder="Search questions..." className="input mb-4" value={search} onChange={e => setSearch(e.target.value)} />
            <div className="space-y-3 max-h-[70vh] overflow-y-auto pr-1">
              {filtered.map(q => (
                <div key={q.id} className="bg-white rounded-lg shadow p-4">
                  <div className="flex justify-between items-start gap-2">
                    <p className="text-sm font-medium flex-1">{q.questionText}</p>
                    <div className="flex gap-2 shrink-0">
                      <button onClick={() => edit(q)} className="text-blue-600 text-xs hover:underline">Edit</button>
                      <button onClick={() => del(q.id)} className="text-red-500 text-xs hover:underline">Del</button>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-1 mt-2 text-xs text-gray-600">
                    {['A','B','C','D'].map(o => <span key={o} className={q.correctOption === o ? 'text-green-600 font-bold' : ''}>{o}. {q[`option${o}`]}</span>)}
                  </div>
                  <div className="flex gap-2 mt-2">
                    <span className="text-xs bg-gray-100 px-2 py-0.5 rounded">{q.subject}</span>
                    <span className="text-xs bg-yellow-100 px-2 py-0.5 rounded">{q.difficulty}</span>
                    <span className="text-xs bg-blue-100 px-2 py-0.5 rounded">{q.marks} mark(s)</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
