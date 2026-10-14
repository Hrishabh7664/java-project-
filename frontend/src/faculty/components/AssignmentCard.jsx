import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Clock } from 'lucide-react';

export const AssignmentCardFaculty = ({ assignment }) => {
  const navigate = useNavigate();

  return (
    <div className="student-card student-card-hover" style={{ cursor: 'pointer' }} onClick={() => navigate(`/faculty/assignments/${assignment.id}`)}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
        <span className="badge badge-trust">{assignment.targetClass}</span>
        <span className="status-badge">{assignment.status}</span>
      </div>

      <h3 style={{ fontSize: '1.15rem', color: 'var(--primary-dark)', marginBottom: '0.4rem' }}>{assignment.title}</h3>
      <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.85rem' }}>{assignment.subject}</p>

      <div style={{ borderTop: '1px solid var(--border-light)', paddingTop: '0.75rem', display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
        <span style={{ color: 'var(--error)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}><Clock size={14} /> Due: {assignment.deadline ? new Date(assignment.deadline).toLocaleString() : '—'}</span>
        <span style={{ color: 'var(--text-muted)' }}>{assignment.facultyName || 'Faculty'}</span>
      </div>
    </div>
  );
};
