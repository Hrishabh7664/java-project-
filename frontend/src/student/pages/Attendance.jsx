import React, { useEffect, useState } from 'react';
import { attendanceService } from '../services/attendanceService';
import { apiErrorMessage } from '../../services/api';

export const Attendance = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  useEffect(() => {
    attendanceService.getAttendance().then(setData)
      .catch((err) => setError(apiErrorMessage(err, 'Your attendance could not be loaded.')))
      .finally(() => setLoading(false));
  }, []);
  if (loading) return <div className="student-card" role="status">Loading your saved attendance…</div>;
  if (error) return <div className="student-card" role="alert">{error}</div>;
  if (!data) return null;

  return <div>
    <div className="student-page-header"><div className="student-page-title"><h1>Attendance Dashboard</h1><p>Your attendance is calculated from faculty-submitted class registers.</p></div></div>
    <div className="student-card" style={{ marginBottom: '1.5rem', background: 'linear-gradient(135deg, #1e3a8a, #0f172a)', color: 'white' }}>
      <h3 style={{ color: 'white', fontSize: '1.2rem', marginBottom: '.4rem' }}>Overall Attendance</h3>
      <div style={{ fontSize: '2.5rem', fontWeight: 800, color: '#38bdf8' }}>{data.overallPercentage}%</div>
      <p style={{ color: '#cbd5e1' }}>Attended {data.classesAttended} of {data.classesHeld} recorded classes · {data.classesMissed} missed</p>
    </div>
    {!data.classesHeld ? <div className="student-card">No attendance has been recorded for your account yet.</div> : <>
      <h2 style={{ marginBottom: '.75rem' }}>By subject</h2>
      <div className="grid-2" style={{ marginBottom: '1.5rem' }}>{data.subjects.map((subject) => <article className="student-card" key={subject.subjectId}>
        <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}><div><h3>{subject.subjectName}</h3><span>{subject.subjectCode}</span></div><strong style={{ color: subject.percentage >= 75 ? 'var(--success)' : 'var(--error)' }}>{subject.percentage}%</strong></div>
        <div style={{ height: 8, background: '#e2e8f0', borderRadius: 6, margin: '.75rem 0', overflow: 'hidden' }}><div style={{ width: `${subject.percentage}%`, height: '100%', background: subject.percentage >= 75 ? 'var(--success)' : 'var(--error)' }} /></div>
        <p>Attended {subject.classesAttended} of {subject.classesHeld} classes · {subject.classesMissed} missed</p>
      </article>)}</div>
      <h2 style={{ marginBottom: '.75rem' }}>Class record</h2>
      <div className="admin-table-container"><table className="admin-table"><thead><tr><th>Date</th><th>Subject</th><th>Lecture</th><th>Status</th></tr></thead>
        <tbody>{data.sessions.map((session) => <tr key={session.id}><td>{session.date}</td><td><strong>{session.subjectCode}</strong> · {session.subjectName}</td><td>Lecture {session.lectureNumber} ({session.startTime}–{session.endTime})</td><td>{session.present ? 'Present' : 'Absent'}</td></tr>)}</tbody>
      </table></div>
    </>}
  </div>;
};
