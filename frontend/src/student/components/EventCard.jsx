import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, MapPin, Clock } from 'lucide-react';

export const EventCard = ({ event, onRegister }) => {
  const navigate = useNavigate();

  return (
    <div className="student-card student-card-hover" style={{ display: 'flex', flexDirection: 'column', height: 'auto', marginBottom: '1.25rem', overflow: 'hidden', minWidth: 0 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem', flexWrap: 'wrap', gap: '0.5rem' }}>
        <span className="badge badge-trust">{event.category}</span>
        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{event.sharingLabel || 'Local event'} · {event.originCampus || 'TSDCEM demo campus'}</span>
      </div>

      <h3
        style={{ fontSize: '1.15rem', color: 'var(--primary-dark)', marginBottom: '0.5rem', cursor: 'pointer', overflowWrap: 'anywhere', wordBreak: 'break-word' }}
        onClick={() => navigate(`/student/events/${event.id}`)}
      >
        {event.title}
      </h3>
      <p style={{ fontSize: '0.85rem', color: 'var(--primary-light)', fontWeight: '600', marginBottom: '0.85rem', overflowWrap: 'anywhere', wordBreak: 'break-word' }}>
        Organizer: {event.organizer}
      </p>

      <div style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.35rem', marginBottom: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Calendar size={15} /> {event.date}</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Clock size={15} /> {event.time}</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><MapPin size={15} /> {event.venue}</div>
      </div>

      <button className="btn btn-outline" onClick={() => navigate(`/student/events/${event.id}`)} style={{ width: '100%' }}>
        View event details
      </button>
    </div>
  );
};
