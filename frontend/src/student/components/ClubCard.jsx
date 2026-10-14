import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, UserCheck } from 'lucide-react';

export const ClubCard = ({ club }) => {
  const navigate = useNavigate();

  return (
    <div
      className="student-card student-card-hover"
      style={{ cursor: 'pointer', display: 'flex', flexDirection: 'column', height: '100%' }}
      onClick={() => navigate(`/student/clubs/${club.id}`)}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
        <div aria-hidden="true" className="club-initials">{club.name?.slice(0, 1).toUpperCase()}</div>
        <div>
          <h3 style={{ fontSize: '1.15rem', color: 'var(--primary-dark)' }}>{club.name}</h3>
          <span className="badge badge-trust" style={{ marginTop: '4px' }}>{club.category}</span>
        </div>
      </div>

      <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '1.25rem', flex: 1, lineHeight: '1.5' }}>
        {club.description}
      </p>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--border-light)', paddingTop: '1rem' }}>
        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <Users size={16} /> {club.membersCount} Members
        </span>
        <span style={{ color: 'var(--primary-light)', fontSize: '0.82rem' }}>{club.isMember ? 'Member · View details' : club.latestApplicationStatus ? `${club.latestApplicationStatus.replaceAll('_',' ')} · View` : 'View details'}</span>
      </div>
    </div>
  );
};
