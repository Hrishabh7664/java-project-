import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, UserCheck } from 'lucide-react';
import { attendanceService } from '../services/attendanceService';
import { apiErrorMessage } from '../../services/api';
import { useSearchParams } from 'react-router-dom';

const nextMonday = () => {
  const date = new Date();
  const day = date.getDay();
  date.setDate(date.getDate() + ((8 - day) % 7));
  return [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'), String(date.getDate()).padStart(2, '0')].join('-');
};

export const TakeAttendance = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [options, setOptions] = useState([]);
  const [assignmentId, setAssignmentId] = useState('');
  const [date, setDate] = useState(() => searchParams.get('date') || nextMonday());
  const [roster, setRoster] = useState(null);
  const [marks, setMarks] = useState({});
  const [loadingOptions, setLoadingOptions] = useState(true);
  const [loadingRoster, setLoadingRoster] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    attendanceService.getOptions().then((data) => {
      setOptions(data);
      if (data.length) setAssignmentId(searchParams.get('assignmentId') || String(data[0].id));
    }).catch((err) => setError(apiErrorMessage(err, 'Assigned classes could not be loaded.')))
      .finally(() => setLoadingOptions(false));
  }, []);

  const loadRoster = useCallback(async () => {
    if (!assignmentId || !date) { setRoster(null); return; }
    setLoadingRoster(true); setError(''); setMessage('');
    try {
      const data = await attendanceService.getRoster(assignmentId, date);
      setRoster(data);
      setMarks(Object.fromEntries(data.students.map((student) => [student.id, student.present ?? true])));
    } catch (err) {
      setRoster(null);
      setError(apiErrorMessage(err, 'The roster could not be loaded.'));
    } finally { setLoadingRoster(false); }
  }, [assignmentId, date]);

  useEffect(() => { loadRoster(); }, [loadRoster]);

  const selected = options.find((option) => String(option.id) === assignmentId);
  const toggle = (studentId) => setMarks((current) => ({ ...current, [studentId]: !current[studentId] }));
  const submit = async (event) => {
    event.preventDefault();
    if (!roster?.students.length) return;
    setSaving(true); setError(''); setMessage('');
    try {
      const saved = await attendanceService.saveAttendance({
        assignmentId: Number(assignmentId), date,
        attendance: roster.students.map((student) => ({ studentId: student.id, present: Boolean(marks[student.id]) }))
      }, roster.sessionId);
      setMessage(roster.sessionId ? 'Attendance correction saved.' : 'Attendance register saved successfully.');
      setRoster((current) => ({ ...current, sessionId: saved.id, students: saved.attendance }));
      setMarks(Object.fromEntries(saved.attendance.map((student) => [student.id, student.present])));
    } catch (err) { setError(apiErrorMessage(err, 'Attendance could not be saved. Please review and try again.')); }
    finally { setSaving(false); }
  };

  return <div>
    <button className="btn btn-outline" style={{ marginBottom: '1.25rem' }} onClick={() => navigate('/faculty/attendance')}><ArrowLeft size={16} /> Back to Attendance Portal</button>
    <div className="student-page-header"><div className="student-page-title"><h1>Take Attendance</h1><p>Select an assigned class, date and scheduled lecture, then review the register before saving.</p></div></div>
    {error && <div className="student-card" role="alert" style={{ marginBottom: '1rem' }}>{error}</div>}
    {message && <div className="student-card" role="status" style={{ marginBottom: '1rem', color: 'var(--success)' }}><CheckCircle2 size={18} /> {message}</div>}
    {loadingOptions ? <div className="student-card" role="status">Loading assigned classes…</div> : !options.length ? <div className="student-card">No classes are assigned to this faculty account.</div> : <>
      <section className="student-card" style={{ marginBottom: '1.25rem', display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem' }}>
        <div className="form-group"><label className="form-label" htmlFor="attendance-assignment">Assigned subject and class</label>
          <select id="attendance-assignment" className="form-input" value={assignmentId} onChange={(e) => setAssignmentId(e.target.value)}>
            {options.map((o) => <option key={o.id} value={o.id}>{o.subjectCode} · {o.subjectName} — {o.departmentCode} {o.academicYear} Div {o.division} Batch {o.batch} · {o.dayOfWeek} {o.startTime}–{o.endTime}</option>)}
          </select></div>
        <div className="form-group"><label className="form-label" htmlFor="attendance-date">Lecture date (scheduled {selected?.dayOfWeek})</label>
          <input id="attendance-date" type="date" className="form-input" value={date} onChange={(e) => setDate(e.target.value)} required /></div>
      </section>
      {loadingRoster ? <div className="student-card" role="status">Loading class roster…</div> : roster && <>
        {roster.sessionId && <p className="student-card" role="status">A register exists for this lecture. Changes will be recorded in the attendance history.</p>}
        {!roster.students.length ? <div className="student-card">No students are assigned to this class and batch.</div> : <form onSubmit={submit}>
          <div className="admin-table-container" style={{ marginBottom: '1rem' }}><table className="attendance-register-table">
            <thead><tr><th>Student</th><th>College email</th><th>Department / Year</th><th>Review status</th></tr></thead>
            <tbody>{roster.students.map((student) => <tr key={student.id}>
              <td style={{ fontWeight: 600 }}>{student.name}</td><td>{student.email}</td>
              <td>{student.department} · {student.academicYear} Div {student.division} Batch {student.batch}</td>
              <td><button type="button" aria-pressed={Boolean(marks[student.id])}
                className={`attendance-toggle-btn ${marks[student.id] ? 'btn-present' : 'btn-absent'}`}
                onClick={() => toggle(student.id)}>{marks[student.id] ? 'Present' : 'Absent'}</button></td>
            </tr>)}</tbody>
          </table></div>
          <p>Review: {Object.values(marks).filter(Boolean).length} present · {Object.values(marks).filter((v) => !v).length} absent · {roster.students.length} students</p>
          <button type="submit" className="btn btn-primary" disabled={saving}><UserCheck size={18} /> {saving ? 'Saving…' : roster.sessionId ? 'Save correction' : 'Save attendance register'}</button>
        </form>}
      </>}
    </>}
  </div>;
};
