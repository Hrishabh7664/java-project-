import React, { useCallback, useEffect, useState } from 'react';
import { Check, RefreshCw, Users, X } from 'lucide-react';
import { clubService } from '../services/clubService';
import { apiErrorMessage } from '../../services/api';

export const ClubLeaderDashboard = () => {
  const [clubs, setClubs] = useState([]);
  const [clubId, setClubId] = useState('');
  const [rows, setRows] = useState([]);
  const [filter, setFilter] = useState('PENDING');
  const [notes, setNotes] = useState({});
  const [loading, setLoading] = useState(true);
  const [rowsLoading, setRowsLoading] = useState(false);
  const [saving, setSaving] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const refreshClubs = useCallback(async (quiet = false) => {
    if (!quiet) setLoading(true);
    try {
      const items = await clubService.getLeaderClubs();
      setClubs(items);
      setClubId((current) => items.some((club) => String(club.id) === current)
        ? current
        : items.length ? String(items[0].id) : '');
      setError('');
    } catch (e) {
      setError(apiErrorMessage(e, 'Your clubs could not be loaded.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshClubs();
    const interval = window.setInterval(() => refreshClubs(true), 15000);
    const onFocus = () => refreshClubs(true);
    window.addEventListener('focus', onFocus);
    return () => {
      window.clearInterval(interval);
      window.removeEventListener('focus', onFocus);
    };
  }, [refreshClubs]);

  useEffect(() => {
    if (!clubId) { setRows([]); return undefined; }
    let active = true;
    setRowsLoading(true);
    setError('');
    clubService.getClubApplications(clubId, filter)
      .then((data) => { if (active) setRows(data); })
      .catch((e) => { if (active) setError(apiErrorMessage(e, 'Applications could not be loaded.')); })
      .finally(() => { if (active) setRowsLoading(false); });
    return () => { active = false; };
  }, [clubId, filter]);

  const decide = async (application, status) => {
    setSaving(application.id); setError(''); setMessage('');
    try {
      const updated = await clubService.decide(application.id, status, notes[application.id] || '');
      setMessage(`Application ${status.toLowerCase()}.`);
      setRows((current) => filter === 'PENDING'
        ? current.filter((row) => row.id !== application.id)
        : current.map((row) => row.id === application.id ? updated : row));
      setNotes((current) => ({ ...current, [application.id]: '' }));
    } catch (e) {
      setError(apiErrorMessage(e, 'The decision could not be saved.'));
    } finally {
      setSaving(null);
    }
  };

  if (loading) return <div className="student-card" role="status">Loading club leadership…</div>;

  return <div>
    <div className="student-page-header">
      <div className="student-page-title">
        <h1>Club join applications</h1>
        <p>Review and decide on membership requests for clubs assigned to your student account.</p>
      </div>
      <button className="btn btn-outline" onClick={() => refreshClubs()} disabled={loading}>
        <RefreshCw size={16} /> Refresh clubs
      </button>
    </div>
    {error && <div role="alert" className="student-card">{error}</div>}
    {message && <div role="status" className="student-card">{message}</div>}
    {!clubs.length && !error && <div className="student-card"><Users size={20} /> No clubs are assigned to your account as a leader yet. This page refreshes automatically.</div>}
    {clubs.length > 0 && <>
      <section className="student-card club-leader-toolbar">
        <label className="club-category-filter">Your club
          <select className="form-input" value={clubId} onChange={(e) => setClubId(e.target.value)}>
            {clubs.map((club) => <option key={club.id} value={club.id}>{club.name}</option>)}
          </select>
        </label>
        <label className="club-category-filter">Application status
          <select className="form-input" value={filter} onChange={(e) => setFilter(e.target.value)}>
            {['PENDING', 'APPROVED', 'REJECTED', 'WITHDRAWN', 'ALL'].map((value) => <option key={value} value={value}>
              {value === 'ALL' ? 'All applications' : value[0] + value.slice(1).toLowerCase()}
            </option>)}
          </select>
        </label>
      </section>
      {rowsLoading ? <div className="student-card" role="status">Loading applications…</div>
        : !rows.length ? <div className="student-card">No {filter === 'ALL' ? '' : `${filter.toLowerCase()} `}applications for this club.</div>
          : <div className="club-application-list">{rows.map((application) => <article className="student-card" key={application.id}>
            <div className="club-application-heading"><div><h2>{application.studentName}</h2><p>{application.studentEmail}</p></div>
              <span className={`club-application-status status-${application.status.toLowerCase()}`}>{application.status}</span>
            </div>
            <div className="club-applicant-facts">{[application.department, application.academicYear,
              application.division && `Division ${application.division}`, application.batch && `Batch ${application.batch}`]
              .filter(Boolean).join(' · ') || 'Academic profile not provided'}</div>
            <p>Applied {new Date(application.submittedAt).toLocaleString()}</p>
            {application.motivation && <div className="club-motivation"><strong>Motivation</strong><p>{application.motivation}</p></div>}
            {application.decidedAt && <div className="club-decision-record"><strong>Decision by {application.decidedByName || 'you'}</strong>
              <span>{new Date(application.decidedAt).toLocaleString()}</span>{application.decisionNote && <p>{application.decisionNote}</p>}
            </div>}
            {application.status === 'PENDING' && <>
              <div className="form-group"><label className="form-label" htmlFor={`decision-note-${application.id}`}>Decision note (optional)</label>
                <textarea id={`decision-note-${application.id}`} className="form-input" rows={2} maxLength={3000}
                  value={notes[application.id] || ''} onChange={(e) => setNotes((current) => ({ ...current, [application.id]: e.target.value }))} />
              </div>
              <div className="club-decision-actions">
                <button className="btn btn-primary" disabled={saving === application.id} onClick={() => decide(application, 'APPROVED')}><Check size={16} /> Approve</button>
                <button className="btn btn-outline" disabled={saving === application.id} onClick={() => decide(application, 'REJECTED')}><X size={16} /> Reject</button>
              </div>
            </>}
          </article>)}</div>}
    </>}
  </div>;
};
