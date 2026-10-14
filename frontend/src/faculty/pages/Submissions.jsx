import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { CheckCircle2, FileCheck, RefreshCw } from 'lucide-react';
import { submissionService } from '../services/submissionService';
import { apiErrorMessage } from '../../services/api';

const displayDate = (v) => v ? new Date(v).toLocaleString() : '—';
export const Submissions = () => {
  const [params] = useSearchParams(); const assignmentId = params.get('assignmentId');
  const [rows, setRows] = useState([]); const [selected, setSelected] = useState(null); const [pdfUrl, setPdfUrl] = useState('');
  const [marks, setMarks] = useState(''); const [feedback, setFeedback] = useState(''); const [action, setAction] = useState('GRADE');
  const [loading, setLoading] = useState(true); const [saving, setSaving] = useState(false); const [error, setError] = useState(''); const [message, setMessage] = useState('');
  const load = async () => { setLoading(true); setError(''); try { setRows(await submissionService.getSubmissions(assignmentId)); } catch (e) { setError(apiErrorMessage(e, 'Submissions could not be loaded.')); } finally { setLoading(false); } };
  useEffect(() => { load(); }, [assignmentId]);
  useEffect(() => { let objectUrl; if (selected?.versions?.length) submissionService.getPdf(selected.id, selected.currentVersion).then((url) => { objectUrl = url; setPdfUrl(url); }).catch((e) => setError(apiErrorMessage(e, 'The PDF could not be opened.'))); else setPdfUrl(''); return () => { if (objectUrl) URL.revokeObjectURL(objectUrl); }; }, [selected]);
  const open = (row) => { setSelected(row); setMarks(row.marks ?? ''); setFeedback(row.feedback || ''); setAction('GRADE'); setMessage(''); setError(''); };
  const review = async (e) => { e.preventDefault(); setSaving(true); setError(''); setMessage(''); try { const updated = await submissionService.review(selected.id, action, marks, feedback); setRows((current) => current.map((r) => r.id === updated.id ? updated : r)); setSelected(updated); setMessage(`Review saved: ${updated.status.replaceAll('_', ' ')}.`); } catch (e2) { setError(apiErrorMessage(e2, 'Review could not be saved.')); } finally { setSaving(false); } };
  return <div><div className="student-page-header"><div className="student-page-title"><h1>Submission review</h1><p>Review PDFs, request revisions, approve work, or save grades with written feedback.</p></div><button className="btn btn-outline" onClick={load}><RefreshCw size={16} /> Refresh</button></div>
    {error && !selected && <div className="student-card" role="alert">{error}</div>}{message && !selected && <div className="student-card" role="status"><CheckCircle2 size={18} /> {message}</div>}
    {loading ? <div className="student-card" role="status">Loading submissions…</div> : !rows.length ? <div className="student-card">No student submissions are ready for review.</div> : <div className="admin-table-container"><table className="admin-table"><thead><tr><th>Student</th><th>Assignment</th><th>Submitted</th><th>Version</th><th>Status</th><th>Marks</th><th /></tr></thead><tbody>
      {rows.map((r) => <tr key={r.id}><td>{r.studentName}<br /><small>{r.studentEmail}</small></td><td><Link to={`/faculty/assignments/${r.assignmentId}`}>{r.assignmentTitle}</Link></td><td>{displayDate(r.submittedAt)}{r.late && <strong> · Late</strong>}</td><td>{r.currentVersion}</td><td>{r.status.replaceAll('_', ' ')}</td><td>{r.marks ?? '—'} / {r.maximumMarks}</td><td><button className="btn btn-primary" onClick={() => open(r)}><FileCheck size={15} /> Review</button></td></tr>)}
    </tbody></table></div>}
    {selected && <div className="modal-overlay"><section className="modal-content-box" style={{ width: 'min(1100px,96vw)', maxHeight: '92vh', overflow: 'auto' }} role="dialog" aria-modal="true" aria-labelledby="review-title">
      <h2 id="review-title">{selected.assignmentTitle}</h2><p><strong>{selected.studentName}</strong> · {selected.studentEmail} · Submitted {displayDate(selected.submittedAt)} · {selected.late ? 'Late' : 'On time'} · Version {selected.currentVersion}</p>
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0,1.6fr) minmax(270px,1fr)', gap: '1rem' }}>
        <div><h3>Submission PDF</h3>{pdfUrl ? <iframe title={`PDF submission by ${selected.studentName}`} src={pdfUrl} style={{ width: '100%', height: '65vh', border: '1px solid var(--border-color,#ddd)', borderRadius: 8 }} /> : <div className="student-card" role="status">Opening the latest PDF…</div>}
          <h3>Version history</h3><ol>{selected.versions.map((v) => <li key={v.version}>Version {v.version}: {v.filename} · {displayDate(v.uploadedAt)} {v.version !== selected.currentVersion && <button type="button" className="btn btn-outline" onClick={async () => setPdfUrl(await submissionService.getPdf(selected.id, v.version))}>View</button>}</li>)}</ol>
        </div>
        <form onSubmit={review}><div className="form-group"><label className="form-label" htmlFor="review-action">Review outcome</label><select id="review-action" className="form-input" value={action} onChange={(e) => setAction(e.target.value)}><option value="GRADE">Grade</option><option value="APPROVE">Approve</option><option value="REQUEST_RESUBMISSION">Request resubmission</option><option value="REJECT">Reject</option></select></div>
          {(action === 'GRADE' || action === 'APPROVE') && <div className="form-group"><label className="form-label" htmlFor="review-marks">Marks (maximum {selected.maximumMarks})</label><input id="review-marks" className="form-input" type="number" min="0" max={selected.maximumMarks} step="0.01" value={marks} onChange={(e) => setMarks(e.target.value)} required /></div>}
          <div className="form-group"><label className="form-label" htmlFor="review-feedback">Written feedback {['REQUEST_RESUBMISSION','REJECT'].includes(action) && '(required)'}</label><textarea id="review-feedback" className="form-input" rows={7} value={feedback} onChange={(e) => setFeedback(e.target.value)} required={['REQUEST_RESUBMISSION','REJECT'].includes(action)} /></div>
          {error && <div role="alert" className="student-card">{error}</div>}{message && <div role="status" className="student-card"><CheckCircle2 size={17} /> {message}</div>}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '.75rem' }}><button type="button" className="btn btn-outline" onClick={() => { setSelected(null); setMessage(''); }} disabled={saving}>Close</button><button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : 'Confirm review'}</button></div>
        </form>
      </div>
    </section></div>}
  </div>;
};
