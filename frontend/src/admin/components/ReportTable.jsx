import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye } from 'lucide-react';

export const ReportTable = ({ reports }) => {
  const navigate = useNavigate();

  return (
    <div className="admin-table-container">
      <table className="admin-table">
        <thead>
          <tr>
            <th>Report ID</th>
            <th>Content</th>
            <th>Reason</th>
            <th>Submitted</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {reports.map((rep) => (
            <tr key={rep.id}>
              <td style={{ fontFamily: 'monospace', fontWeight: '600' }}>{rep.id}</td>
              <td style={{ fontWeight: '600', color: 'var(--primary-dark)' }}>{rep.contentType} #{rep.contentId}</td>
              <td>{rep.reason}</td>
              <td>{rep.createdAt ? new Date(rep.createdAt).toLocaleString() : '—'}</td>
              <td>
                <span className={`status-badge ${rep.status === 'RESOLVED' || rep.status === 'DISMISSED' ? 'status-published' : 'status-pending'}`}>{rep.status}</span>
              </td>
              <td>
                <button className="btn btn-primary" style={{ padding: '0.3rem 0.75rem', fontSize: '0.78rem' }} onClick={() => navigate(`/admin/reports/${rep.id}`,{state:{report:rep}})}>
                  <Eye size={14} /> Review
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
