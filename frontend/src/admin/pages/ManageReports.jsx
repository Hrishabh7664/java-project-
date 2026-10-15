import React, { useCallback, useEffect, useState } from 'react';
import { RefreshCw, Search } from 'lucide-react';
import { reportService } from '../services/reportService';
import { ReportTable } from '../components/ReportTable';
import { apiErrorMessage } from '../../services/api';

const complaintStatuses = ['Submitted', 'Under review', 'Investigating', 'Resolved', 'Rejected'];

export const ManageReports = () => {
  const [reports, setReports] = useState([]);
  const [complaints, setComplaints] = useState([]);
  const [search, setSearch] = useState('');
  const [tab, setTab] = useState('complaints');
  const [loading, setLoading] = useState(true);
  const [complaintsLoading, setComplaintsLoading] = useState(true);
  const [savingComplaint, setSavingComplaint] = useState(null);
  const [error, setError] = useState('');
  const [complaintError, setComplaintError] = useState('');
  const [message, setMessage] = useState('');

  const loadReports = useCallback(async () => {
    setLoading(true);
    setError('');
    try { setReports(await reportService.getModerationReports()); }
    catch (requestError) { setError(apiErrorMessage(requestError, 'Content reports could not be loaded.')); }
    finally { setLoading(false); }
  }, []);

  const loadComplaints = useCallback(async () => {
    setComplaintsLoading(true);
    setComplaintError('');
    try { setComplaints(await reportService.getReports()); }
    catch (requestError) { setComplaintError(apiErrorMessage(requestError, 'Student complaints could not be loaded.')); }
    finally { setComplaintsLoading(false); }
  }, []);

  useEffect(() => { loadReports(); loadComplaints(); }, [loadReports, loadComplaints]);

  const filtered = reports.filter((report) => `${report.contentType} ${report.contentId} ${report.reason} ${report.status}`.toLowerCase().includes(search.toLowerCase()));

  const updateComplaintStatus = async (complaint, status) => {
    setSavingComplaint(complaint.id);
    setComplaintError('');
    setMessage('');
    try {
      const updated = await reportService.updateReport(complaint.id, { ...complaint, status });
      setComplaints((items) => items.map((item) => item.id === updated.id ? updated : item));
      setMessage(`Complaint #${complaint.id} updated.`);
    } catch (requestError) {
      setComplaintError(apiErrorMessage(requestError, 'The complaint status could not be updated.'));
    } finally {
      setSavingComplaint(null);
    }
  };

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title"><h1>Reports &amp; complaints</h1><p>Review student grievances and reports submitted for discussion posts and comments.</p></div>
      </div>
      <div className="admin-toolbar" role="tablist" aria-label="Report type">
        <button type="button" role="tab" aria-selected={tab === 'complaints'} className={`btn ${tab === 'complaints' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setTab('complaints')}>Student complaints ({complaints.length})</button>
        <button type="button" role="tab" aria-selected={tab === 'content'} className={`btn ${tab === 'content' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setTab('content')}>Content reports ({reports.length})</button>
      </div>

      {tab === 'complaints' && <section role="tabpanel">
        {message && <div className="success-notice" role="status">{message}</div>}
        {complaintError && <div className="student-card api-error-state" role="alert"><span>{complaintError}</span><button className="btn btn-outline" onClick={loadComplaints}><RefreshCw size={15} /> Retry</button></div>}
        {complaintsLoading && <div className="student-card" role="status">Loading student complaints…</div>}
        {!complaintsLoading && !complaintError && complaints.length === 0 && <div className="student-card compact-empty">No student complaints have been submitted.</div>}
        {!complaintsLoading && complaints.map((complaint) => <article className="student-card" key={complaint.id}>
          <div className="complaint-record-head"><span className="badge badge-college">{complaint.category || 'General'}</span><span className="status-badge status-pending">{complaint.status}</span></div>
          <h2>{complaint.subject}</h2>
          <p>{complaint.description}</p>
          <p><strong>Submitted by:</strong> {complaint.anonymous ? 'Anonymous' : (complaint.submittedBy || 'Student')}</p>
          <div className="form-actions">
            <label className="form-label" htmlFor={`complaint-status-${complaint.id}`}>Status</label>
            <select id={`complaint-status-${complaint.id}`} className="form-input" value={complaint.status} disabled={savingComplaint === complaint.id} onChange={(event) => updateComplaintStatus(complaint, event.target.value)}>
              {complaintStatuses.map((status) => <option key={status} value={status}>{status}</option>)}
            </select>
            {savingComplaint === complaint.id && <span role="status">Saving…</span>}
          </div>
        </article>)}
      </section>}

      {tab === 'content' && <section role="tabpanel">
        <div className="admin-toolbar">
          <div className="admin-search-wrapper"><Search size={16} className="admin-search-icon" /><input className="admin-search-input" type="search" placeholder="Search type, reason, or status" value={search} onChange={(event) => setSearch(event.target.value)} /></div>
        </div>
        {error && <div className="student-card api-error-state" role="alert"><span>{error}</span><button className="btn btn-outline" onClick={loadReports}><RefreshCw size={15} /> Retry</button></div>}
        {loading && <div className="student-card" role="status">Loading content reports…</div>}
        {!loading && !error && filtered.length > 0 && <ReportTable reports={filtered} />}
        {!loading && !error && filtered.length === 0 && <div className="student-card compact-empty">{search ? 'No reports match this search.' : 'No content reports have been submitted.'}</div>}
      </section>}
    </div>
  );
};
