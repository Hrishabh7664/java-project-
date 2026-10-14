import React, { useState, useEffect } from 'react';
import { electionManagementService } from '../services/electionManagementService';
import { apiErrorMessage } from '../../services/api';

export const CandidateManagement = () => {
  const [elections, setElections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    async function load() {
      try {
        setElections(await electionManagementService.getElections());
      } catch (requestError) {
        setError(apiErrorMessage(requestError, 'Candidate applications could not be loaded.'));
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  const handleUpdate = async (electionId, candidateId, approved) => {
    setError('');
    try {
      await electionManagementService.updateCandidateStatus(electionId, candidateId, approved);
      setMessage(`Candidate ${approved ? 'approved' : 'rejected'}.`);
      setElections(await electionManagementService.getElections());
    } catch (requestError) {
      setError(apiErrorMessage(requestError, 'Candidate review could not be saved.'));
    }
  };

  const candidates = elections.flatMap(election => (election.candidates || []).map(candidate => ({ ...candidate, electionId: election.id, electionTitle: election.title, position: election.position })));

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>Candidate Application Review</h1>
          <p>Approve or reject student nominations for campus elections</p>
        </div>
      </div>

      {error && <p role="alert" className="validation-error">{error}</p>}
      {message && <p role="status" className="success-notice">{message}</p>}
      {loading && <p role="status">Loading candidate applications…</p>}

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Candidate Name</th>
              <th>Email & Branch</th>
              <th>Election & Position</th>
              <th>Manifesto</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {!loading && candidates.length === 0 && <tr><td colSpan="6">No candidate applications are available.</td></tr>}
            {candidates.map(cand => (
              <tr key={`${cand.electionId}-${cand.id}`}>
                <td style={{ fontWeight: '600', color: 'var(--primary-dark)' }}>{cand.name}</td>
                <td>{cand.department || '—'} ({cand.year || '—'})</td>
                <td>{cand.electionTitle} — <strong>{cand.position}</strong></td>
                <td style={{ maxWidth: '300px', fontSize: '0.85rem' }}>{cand.manifesto}</td>
                <td><span className={`status-badge ${cand.approved ? 'status-active' : 'status-pending'}`}>{cand.approved ? 'Approved' : 'Needs review'}</span></td>
                <td>
                  <div style={{ display: 'flex', gap: '0.35rem' }}>
                    <button className="btn btn-outline" disabled={cand.approved} onClick={() => handleUpdate(cand.electionId, cand.id, true)}>Approve</button>
                    <button className="btn btn-outline" disabled={!cand.approved} onClick={() => handleUpdate(cand.electionId, cand.id, false)}>Reject</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
