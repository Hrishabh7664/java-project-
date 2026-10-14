import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, PlusCircle } from 'lucide-react';
import { assignmentService } from '../services/assignmentService';
import { apiErrorMessage } from '../../services/api';

export const CreateAssignment = () => {
  const navigate = useNavigate();
  const [options, setOptions] = useState([]);
  const [form, setForm] = useState({ title: '', description: '', deadlineAt: '', maximumMarks: '20', status: 'PUBLISHED', allowResubmission: true, allowLateSubmission: false });
  const [scheduleId, setScheduleId] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  useEffect(() => {
    assignmentService.getTeachingOptions().then((rows) => { setOptions(rows); if (rows.length) setScheduleId(String(rows[0].id)); })
      .catch((e) => setError(apiErrorMessage(e, 'Your teaching assignments could not be loaded.'))).finally(() => setLoading(false));
  }, []);
  const selected = options.find((o) => String(o.id) === scheduleId);
  const change = (key) => (e) => setForm((prev) => ({ ...prev, [key]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }));
  const submit = async (e) => {
    e.preventDefault(); if (!selected) return;
    setSaving(true); setError(''); setMessage('');
    try {
      await assignmentService.createAssignment({ ...form, subjectId: selected.subjectId, classId: selected.classId, maximumMarks: Number(form.maximumMarks), deadlineAt: form.deadlineAt, allowResubmission: form.allowResubmission, allowLateSubmission: form.allowLateSubmission });
      setMessage(form.status === 'PUBLISHED' ? 'Assignment published to the selected class.' : 'Draft saved. Students cannot see draft assignments.');
      setTimeout(() => navigate('/faculty/assignments'), 900);
    } catch (e2) { setError(apiErrorMessage(e2, 'Assignment could not be saved.')); }
    finally { setSaving(false); }
  };
  return <div>
    <button className="btn btn-outline" style={{ marginBottom: '1.25rem' }} onClick={() => navigate('/faculty/assignments')}><ArrowLeft size={16} /> Back to Assignments</button>
    <section className="student-card"><h1>Create an assignment</h1><p>Choose a class and subject from your scheduled teaching assignments.</p>
      {loading ? <p role="status">Loading assigned classes…</p> : !options.length ? <div className="student-card">No teaching assignments are linked to your account, so an assignment cannot be created.</div> : <form onSubmit={submit}>
        {error && <div className="student-card" role="alert">{error}</div>}{message && <div className="student-card" role="status">{message}</div>}
        <div className="form-group"><label className="form-label" htmlFor="assignment-schedule">Subject and target class</label><select id="assignment-schedule" className="form-input" value={scheduleId} onChange={(e) => setScheduleId(e.target.value)}>{options.map((o) => <option key={o.id} value={o.id}>{o.subjectCode} · {o.subjectName} — {o.departmentCode} {o.academicYear}, Division {o.division}, Batch {o.batch}</option>)}</select></div>
        {selected && <p className="form-hint">Semester {selected.semester || '—'} · {selected.dayOfWeek}, {selected.startTime}–{selected.endTime} · {selected.classroom || 'Room not set'}</p>}
        <div className="form-group"><label className="form-label" htmlFor="assignment-title">Title</label><input id="assignment-title" className="form-input" maxLength={180} value={form.title} onChange={change('title')} required /></div>
        <div className="form-group"><label className="form-label" htmlFor="assignment-description">Instructions</label><textarea id="assignment-description" className="form-input" rows={6} maxLength={12000} value={form.description} onChange={change('description')} required /></div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(220px,1fr))', gap: '1rem' }}>
          <div className="form-group"><label className="form-label" htmlFor="assignment-deadline">Deadline</label><input id="assignment-deadline" type="datetime-local" className="form-input" value={form.deadlineAt} onChange={change('deadlineAt')} required /></div>
          <div className="form-group"><label className="form-label" htmlFor="assignment-marks">Maximum marks</label><input id="assignment-marks" type="number" min="0.01" max="10000" step="0.01" className="form-input" value={form.maximumMarks} onChange={change('maximumMarks')} required /></div>
          <div className="form-group"><label className="form-label" htmlFor="assignment-status">Availability</label><select id="assignment-status" className="form-input" value={form.status} onChange={change('status')}><option value="DRAFT">Save as draft</option><option value="PUBLISHED">Publish now</option></select></div>
        </div>
        <label style={{ display: 'block', margin: '.5rem 0' }}><input type="checkbox" checked={form.allowResubmission} onChange={change('allowResubmission')} /> Allow resubmission after faculty request</label>
        <label style={{ display: 'block', margin: '.5rem 0 1.25rem' }}><input type="checkbox" checked={form.allowLateSubmission} onChange={change('allowLateSubmission')} /> Accept submissions after the deadline (marked late)</label>
        <button type="submit" className="btn btn-primary" disabled={saving}><PlusCircle size={18} /> {saving ? 'Saving…' : form.status === 'PUBLISHED' ? 'Publish assignment' : 'Save draft'}</button>
      </form>}
    </section>
  </div>;
};
