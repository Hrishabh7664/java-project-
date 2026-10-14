import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Clock } from 'lucide-react';

export const AssignmentCard = ({ assignment }) => {
  const navigate = useNavigate();
  const status = assignment.submissionStatus ? assignment.submissionStatus.replaceAll('_', ' ') : 'Not submitted';

  return (
    <div className="student-card student-card-hover" style={{ cursor: 'pointer' }} onClick={() => navigate(`/student/assignments/${assignment.id}`)}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.6rem' }}>
        <span className="badge badge-trust">{assignment.subject}</span>
        <span className="badge badge-trust" style={{ background: assignment.marks !== null && assignment.marks !== undefined ? '#dcfce7' : '#fef3c7', color: assignment.marks !== null && assignment.marks !== undefined ? '#15803d' : '#b45309' }}>
          {status}
        </span>
      </div>

      <h3 style={{ fontSize: '1.15rem', color: 'var(--primary-dark)', marginBottom: '0.4rem' }}>{assignment.title}</h3>
      <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.85rem' }}>Faculty: {assignment.facultyName}</p>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--border-light)', paddingTop: '0.75rem', fontSize: '0.85rem' }}>
        <span style={{ color: 'var(--error)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}><Clock size={14} /> Due: {assignment.deadline ? new Date(assignment.deadline).toLocaleString() : '—'}</span>
        {assignment.marks !== null && <span style={{ fontWeight: '700', color: 'var(--success)' }}>Marks: {assignment.marks}/{assignment.totalMarks}</span>}
      </div>
    </div>
  );
};
