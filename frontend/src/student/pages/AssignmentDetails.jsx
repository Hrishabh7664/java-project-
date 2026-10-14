import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, CalendarClock, FileUp, CheckCircle2 } from 'lucide-react';
import { assignmentService } from '../services/assignmentService';
import { submissionService } from '../services/submissionService';
import { apiErrorMessage } from '../../services/api';

const dateText = (value) => value ? new Date(value).toLocaleString() : 'Not set';
export const AssignmentDetails = () => {
  const { id } = useParams(); const navigate = useNavigate();
  const [assignment, setAssignment] = useState(null); const [submission, setSubmission] = useState(null);
  const [file, setFile] = useState(null); const [loading, setLoading] = useState(true); const [saving, setSaving] = useState(false);
  const [error, setError] = useState(''); const [message, setMessage] = useState(''); const [confirm, setConfirm] = useState(false);
  useEffect(() => { let active = true;
    Promise.all([assignmentService.getAssignmentById(id), submissionService.getMine(id).catch((e) => e.response?.status === 404 ? null : Promise.reject(e))])
      .then(([a, s]) => { if (active) { setAssignment(a); setSubmission(s); } })
      .catch((e) => { if (active) setError(apiErrorMessage(e, 'Assignment could not be loaded.')); }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [id]);
  const canSubmit = !submission || submission.status === 'RESUBMISSION_REQUESTED' && assignment?.allowResubmission;
  const send = async () => {
    setSaving(true); setError(''); setMessage('');
    try { const saved = await submissionService.submit(id, file); setSubmission(saved); setFile(null); setConfirm(false); setMessage('Your PDF was submitted successfully.'); }
    catch (e) { setError(apiErrorMessage(e, 'Submission failed. Your file has not been confirmed as submitted.')); }
    finally { setSaving(false); }
  };
  if (loading) return <div className="student-card" role="status">Loading assignment…</div>;
  if (error && !assignment) return <div><div className="student-card" role="alert">{error}</div><button className="btn btn-outline" onClick={() => navigate('/student/assignments')}>Back to assignments</button></div>;
  return <div><button className="btn btn-outline" style={{ marginBottom: '1.25rem' }} onClick={() => navigate('/student/assignments')}><ArrowLeft size={16} /> Back to assignments</button>
    <section className="student-card"><span className="badge badge-trust">{assignment.subjectCode} · {assignment.subjectName}</span><h1 style={{ color: 'var(--primary-dark)', margin: '.6rem 0' }}>{assignment.title}</h1>
      <p><CalendarClock size={16} style={{ verticalAlign: 'middle', marginRight: 6 }} />Due {dateText(assignment.deadlineAt)} · {assignment.maximumMarks} marks</p>
      <p>{assignment.departmentCode} · {assignment.academicYear} · Division {assignment.division} · Batch {assignment.batch} · Posted by {assignment.facultyName}</p>
      <h2>Instructions</h2><p style={{ whiteSpace: 'pre-wrap' }}>{assignment.description}</p>
    </section>
    <section className="student-card" style={{ marginTop: '1rem' }}><h2>Your submission</h2>
      {error && <div role="alert" className="student-card">{error}</div>}{message && <div role="status" className="student-card"><CheckCircle2 size={18} /> {message}</div>}
      {submission && <><p><strong>Status:</strong> {submission.status.replaceAll('_', ' ')} · <strong>Submitted:</strong> {dateText(submission.submittedAt)}{submission.late ? ' · Late' : ''}</p>
        {submission.marks !== null && <p><strong>Grade:</strong> {submission.marks}/{submission.maximumMarks}</p>}{submission.feedback && <p><strong>Faculty feedback:</strong> {submission.feedback}</p>}
        <h3>Submission history</h3><ol>{submission.versions.map((v) => <li key={v.version}>Version {v.version}: {v.filename} · {dateText(v.uploadedAt)}</li>)}</ol></>}
      {!canSubmit ? <p>{submission?.status === 'RESUBMISSION_REQUESTED' ? 'Resubmission is disabled for this assignment.' : 'A submission has been recorded. Contact your faculty member if you need to resubmit.'}</p> : <>
        <p>{submission ? 'Faculty requested a new version. Choose a PDF to resubmit.' : 'Upload one PDF file. You will review and confirm before the upload is sent.'}</p>
        <div className="form-group"><label className="form-label" htmlFor="submission-pdf">PDF file (up to 10 MB)</label><input id="submission-pdf" type="file" accept="application/pdf,.pdf" className="form-input" onChange={(e) => setFile(e.target.files?.[0] || null)} /></div>
        <button className="btn btn-primary" disabled={!file || saving} onClick={() => setConfirm(true)}><FileUp size={18} /> Review and submit</button>
      </>}
    </section>
    {confirm && <div className="modal-overlay"><div className="modal-content-box" role="dialog" aria-modal="true" aria-labelledby="submit-confirm-title"><h2 id="submit-confirm-title">Confirm submission</h2><p>Submit <strong>{file?.name}</strong> to “{assignment.title}”? {submission ? 'This creates a new submission version.' : 'Repeated clicks will not create duplicate submissions.'}</p>
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '.75rem' }}><button className="btn btn-outline" onClick={() => setConfirm(false)} disabled={saving}>Cancel</button><button className="btn btn-primary" onClick={send} disabled={saving}>{saving ? 'Submitting…' : 'Confirm submission'}</button></div></div></div>}
  </div>;
};
