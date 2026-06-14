import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import Navbar from '../../components/Navbar'
import api from '../../api/axios'

export default function MyResults() {
  const [results, setResults] = useState([])
  const { state } = useLocation()

  useEffect(() => { api.get('/results').then(r => setResults(r.data)) }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto p-6">
        <h2 className="text-2xl font-bold mb-6">My Results</h2>
        {state?.result && (
          <div className={`mb-6 p-5 rounded-xl text-white ${state.result.passed ? 'bg-green-500' : 'bg-red-500'}`}>
            <h3 className="text-lg font-bold">{state.result.passed ? '🎉 Congratulations! You Passed!' : 'You did not pass this time'}</h3>
            <p className="mt-1">Score: {state.result.score}/{state.result.totalMarks} ({state.result.percentage?.toFixed(1)}%)</p>
          </div>
        )}
        <div className="space-y-4">
          {results.map(r => (
            <div key={r.id} className="bg-white rounded-xl shadow p-5 flex justify-between items-center">
              <div>
                <h3 className="font-bold">{r.exam?.title}</h3>
                <p className="text-sm text-gray-500">Score: {r.score}/{r.totalMarks} • {r.percentage?.toFixed(1)}%</p>
                <p className="text-xs text-gray-400">{new Date(r.submittedAt).toLocaleString()}</p>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${r.passed ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
                {r.passed ? 'Passed' : 'Failed'}
              </span>
            </div>
          ))}
          {results.length === 0 && <p className="text-center text-gray-500 py-12">No results yet. Take an exam!</p>}
        </div>
      </div>
    </div>
  )
}
