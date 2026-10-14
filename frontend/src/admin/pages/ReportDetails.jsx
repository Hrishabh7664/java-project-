import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { reportService } from '../services/reportService';
import { ArrowLeft, Save, RefreshCw } from 'lucide-react';
import { apiErrorMessage } from '../../services/api';

export const ReportDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [report, setReport] = useState(location.state?.report || null);
  const [content, setContent] = useState(null);
  const [action, setAction] = useState('HIDE');
  const [reason, setReason] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(false);

  const loadReport = async () => {
    setLoading(true);
    setError('');
    try {
      const result = location.state?.report || (await reportService.getModerationReports()).find(item=>String(item.id)===String(id));
      if(!result) throw new Error('Report not found');
      setReport(result);
      setContent(await reportService.getModerationContent(result.contentType,result.contentId));
    } catch (requestError) {
      setError(apiErrorMessage(requestError, 'This report could not be loaded.'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadReport(); }, [id]);

  const handleSave = async (event) => {
    event.preventDefault();
    if (!report || saving) return;
    setSaving(true);
    setError('');
    setSaved(false);
    try {
      if(!window.confirm(`Confirm moderation action: ${action.toLowerCase()}?`)){setSaving(false);return;}
      const updated = await reportService.resolveModerationReport(report.id, action, reason.trim());
      setReport(updated);
      setReason('');
      setSaved(true);
    } catch (requestError) {
      setError(apiErrorMessage(requestError, 'The report status could not be saved.'));
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="student-card" role="status">Loading report details…</div>;
  if (error && !report) return <div><div className="student-card api-error-state" role="alert"><span>{error}</span><button className="btn btn-outline" onClick={loadReport}><RefreshCw size={15} /> Retry</button></div><button className="btn btn-outline" onClick={() => navigate('/admin/reports')}><ArrowLeft size={16} /> Back to reports</button></div>;
  if (!report) return null;

  return (
    <div className="workspace-page">
      <button className="btn btn-outline report-back-button" onClick={() => navigate('/admin/reports')}><ArrowLeft size={16} /> Back to reports</button>
      {error && <div className="student-card api-error-state" role="alert">{error}</div>}
      {saved && <div className="success-notice" role="status">Moderation decision saved.</div>}

      <section className="student-card report-detail-card">
        <div className="report-detail-meta"><span className="badge badge-college">{report.contentType} #{report.contentId}</span><span className="status-badge status-pending">{report.status}</span></div>
        <h1>Content report</h1>
        {content && <div className="student-card"><h2>{content.title || `Comment #${content.id}`}</h2><p className="report-description">{content.content}</p><span>{content.authorName} · {content.createdAt}</span></div>}
        <p className="report-description"><strong>Reason:</strong> {report.reason}</p>
        <p className="report-description"><strong>Submitted:</strong> {report.createdAt ? new Date(report.createdAt).toLocaleString() : '—'}</p>
        {report.moderationReason && <p className="report-description"><strong>Decision:</strong> {report.moderationReason} · {report.resolvedBy} · {report.resolvedAt ? new Date(report.resolvedAt).toLocaleString() : ''}</p>}
      </section>

      <section className="student-card">
        <div className="student-section-heading"><div><span className="section-kicker">REVIEW</span><h2>Moderation decision</h2></div></div>
        <form onSubmit={handleSave}>
          <div className="form-group">
            <label className="form-label" htmlFor="report-action">Action</label>
            <select id="report-action" className="form-input" value={action} onChange={event=>setAction(event.target.value)}>
              <option value="HIDE">Hide</option><option value="RESTORE">Restore</option><option value="DISMISS">Dismiss report</option>
            </select>
          </div>
          <div className="form-group"><label className="form-label" htmlFor="decision-reason">Decision reason</label><textarea id="decision-reason" className="form-input" value={reason} onChange={e=>setReason(e.target.value)} maxLength={1000} required rows={3}/></div>
          <button type="submit" className="btn btn-primary" disabled={saving || !reason.trim()}><Save size={16} /> {saving ? 'Saving…' : 'Apply decision'}</button>
        </form>
      </section>
    </div>
  );
};
