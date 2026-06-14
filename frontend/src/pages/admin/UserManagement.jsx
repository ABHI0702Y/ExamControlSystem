import { useEffect, useState } from 'react'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

const EMPTY = { username: '', password: '', fullName: '', email: '', role: 'STUDENT', active: true }

export default function UserManagement() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(EMPTY)
  const [editing, setEditing] = useState(null)
  const [error, setError] = useState('')

  const load = () => api.get('/users').then(r => setUsers(r.data))
  useEffect(() => { load() }, [])

  const save = async e => {
    e.preventDefault()
    setError('')
    try {
      if (editing) await api.put(`/users/${editing}`, form)
      else await api.post('/users', form)
      setForm(EMPTY); setEditing(null); load()
    } catch (err) {
      setError(err.response?.data || 'Error saving user')
    }
  }

  const edit = u => {
    setEditing(u.id)
    setForm({ username: u.username, password: '', fullName: u.fullName, email: u.email, role: u.role, active: u.active })
  }

  const del = async id => {
    if (confirm('Delete this user?')) { await api.delete(`/users/${id}`); load() }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto p-6">
        <h2 className="text-2xl font-bold mb-6">User Management</h2>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="bg-white rounded-xl shadow p-6">
            <h3 className="font-bold text-lg mb-4">{editing ? 'Edit User' : 'Add User'}</h3>
            {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
            <form onSubmit={save} className="space-y-3">
              {!editing && <input placeholder="Username" required className="input" value={form.username} onChange={e => setForm({...form, username: e.target.value})} />}
              <input placeholder={editing ? 'New password (optional)' : 'Password'} type="password" required={!editing} className="input" value={form.password} onChange={e => setForm({...form, password: e.target.value})} />
              <input placeholder="Full Name" required className="input" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} />
              <input placeholder="Email" type="email" required className="input" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
              <select className="input" value={form.role} onChange={e => setForm({...form, role: e.target.value})}>
                <option>STUDENT</option><option>TEACHER</option><option>ADMIN</option>
              </select>
              {editing && <label className="flex items-center gap-2 text-sm"><input type="checkbox" checked={form.active} onChange={e => setForm({...form, active: e.target.checked})} /> Active</label>}
              <div className="flex gap-2">
                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 flex-1">{editing ? 'Update' : 'Add'}</button>
                {editing && <button type="button" onClick={() => {setEditing(null); setForm(EMPTY)}} className="bg-gray-200 px-4 py-2 rounded">Cancel</button>}
              </div>
            </form>
          </div>
          <div className="lg:col-span-2 bg-white rounded-xl shadow overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-left"><tr>
                <th className="px-4 py-3">Name</th><th className="px-4 py-3">Username</th>
                <th className="px-4 py-3">Role</th><th className="px-4 py-3">Status</th><th className="px-4 py-3">Actions</th>
              </tr></thead>
              <tbody>{users.map(u => (
                <tr key={u.id} className="border-t hover:bg-gray-50">
                  <td className="px-4 py-3">{u.fullName}</td>
                  <td className="px-4 py-3">{u.username}</td>
                  <td className="px-4 py-3"><span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded text-xs">{u.role}</span></td>
                  <td className="px-4 py-3"><span className={`px-2 py-0.5 rounded text-xs ${u.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{u.active ? 'Active' : 'Inactive'}</span></td>
                  <td className="px-4 py-3 flex gap-2">
                    <button onClick={() => edit(u)} className="text-blue-600 hover:underline text-xs">Edit</button>
                    <button onClick={() => del(u.id)} className="text-red-500 hover:underline text-xs">Delete</button>
                  </td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
