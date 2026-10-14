import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { eventService } from '../services/eventService';
import { ArrowLeft, Calendar, MapPin, Clock } from 'lucide-react';
import { apiErrorMessage } from '../../services/api';

export const EventDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [registered, setRegistered] = useState(false);
  const [count, setCount] = useState(0);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      try {
        const [ev, mine, registrations] = await Promise.all([
          eventService.getEventById(id), eventService.getMyRegistrations(), eventService.getRegistrationCount(id)
        ]);
        setEvent(ev);
        setRegistered(mine.some((registration) => registration.event.id === Number(id)));
        setCount(registrations);
      } catch (requestError) {
        setError(apiErrorMessage(requestError, 'Event details could not be loaded.'));
      }
    }
    load();
  }, [id]);

  const toggleRegistration = async () => {
    setBusy(true); setError(''); setMessage('');
    try {
      if (registered) {
        await eventService.cancelRegistration(id);
        setRegistered(false); setCount((value) => Math.max(0, value - 1)); setMessage('Your registration was cancelled.');
      } else {
        await eventService.register(id);
        setRegistered(true); setCount((value) => value + 1); setMessage('You are registered for this event.');
      }
    } catch (requestError) {
      setError(apiErrorMessage(requestError, 'Registration could not be updated.'));
    } finally { setBusy(false); }
  };

  if (error && !event) return <div className="student-card" role="alert">{error}</div>;
  if (!event) return <div>Loading event details...</div>;

  return (
    <div>
      <button className="btn btn-outline" style={{ marginBottom: '1.25rem' }} onClick={() => navigate('/student/events')}>
        <ArrowLeft size={16} /> Back to Events
      </button>

      <div className="student-card">
        <span className="badge badge-trust" style={{ marginBottom: '0.5rem' }}>{event.category}</span>
        <h1 style={{ fontSize: '1.8rem', color: 'var(--primary-dark)', marginBottom: '0.4rem' }}>{event.title}</h1>
        <p style={{ color: 'var(--primary-light)', fontWeight: '600', marginBottom: '1rem' }}>Organized by {event.organizer}</p>

        <div style={{ background: '#f8fafc', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', marginBottom: '1.25rem', display: 'flex', gap: '2rem' }}>
          <div><Calendar size={16} /> {event.date}</div>
          <div><Clock size={16} /> {event.time}</div>
          <div><MapPin size={16} /> {event.venue}</div>
        </div>

        <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>{event.description}</p>
        <p>{event.eligibility ? `Eligibility: ${event.eligibility}` : 'Open to eligible campus participants'} · {count} registered{event.capacity != null ? ` / ${event.capacity} seats` : ''}</p>
        {event.registrationDeadline && <p>Register by {event.registrationDeadline}</p>}
        {error && <p role="alert" style={{ color: 'var(--danger, #b42318)' }}>{error}</p>}
        {message && <p role="status">{message}</p>}
        <button className={registered ? 'btn btn-outline' : 'btn btn-primary'} onClick={toggleRegistration} disabled={busy || (!registered && event.capacity != null && count >= event.capacity)}>
          {busy ? 'Saving…' : registered ? 'Cancel registration' : count >= event.capacity && event.capacity != null ? 'Event full' : 'Register for event'}
        </button>
      </div>
    </div>
  );
};
