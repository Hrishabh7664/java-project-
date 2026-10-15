import React, { useState, useEffect } from 'react';
import { eventManagementService } from '../services/eventManagementService';
import { PlusCircle, Users, X } from 'lucide-react';

export const ManageEvents = () => {
  const [events, setEvents] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [title, setTitle] = useState('');
  const [organizer, setOrganizer] = useState('College Administration');
  const [date, setDate] = useState('');
  const [venue, setVenue] = useState('');
  const [registrantEvent, setRegistrantEvent] = useState(null);
  const [registrants, setRegistrants] = useState([]);
  const [registrantsLoading, setRegistrantsLoading] = useState(false);
  const [registrantsError, setRegistrantsError] = useState('');

  useEffect(() => {
    async function load() {
      const evs = await eventManagementService.getEvents();
      setEvents(evs);
    }
    load();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!title || !date || !venue) return;
    await eventManagementService.createEvent({ title, organizer, date, venue });
    const updated = await eventManagementService.getEvents();
    setEvents(updated);
    setTitle('');
    setDate('');
    setVenue('');
    setShowModal(false);
  };

  const handleDelete = async (id) => {
    await eventManagementService.deleteEvent(id);
    setEvents((items) => items.filter((event) => event.id !== id));
  };

  const viewRegistrants = async (event) => {
    setRegistrantEvent(event);
    setRegistrants([]);
    setRegistrantsError('');
    setRegistrantsLoading(true);
    try {
      setRegistrants(await eventManagementService.getRegistrants(event.id));
    } catch (error) {
      setRegistrantsError(error.response?.data?.message || 'Could not load event registrations.');
    } finally {
      setRegistrantsLoading(false);
    }
  };

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>Event Management</h1>
          <p>Oversee, publish, or cancel college-wide and organization events</p>
        </div>

        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <PlusCircle size={18} /> Create New Event
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Event Title</th>
              <th>Organizer</th>
              <th>Date & Time</th>
              <th>Venue</th>
              <th>Registrations</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {events.map(ev => (
              <tr key={ev.id}>
                <td style={{ fontWeight: '600', color: 'var(--primary-dark)' }}>{ev.title}</td>
                <td>{ev.organizer}</td>
                <td>{ev.date} ({ev.time})</td>
                <td>{ev.venue}</td>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                    <span>{ev.registrationsCount ?? 0}</span>
                    <button className="btn btn-outline" style={{ padding: '0.3rem 0.6rem', fontSize: '0.78rem' }} onClick={() => viewRegistrants(ev)}>
                      <Users size={14} /> View students
                    </button>
                  </div>
                </td>
              <td><span className="status-badge status-published">{ev.status}</span></td>
                <td>
                  <button className="btn btn-outline" style={{ padding: '0.3rem 0.6rem', fontSize: '0.78rem' }} onClick={() => handleDelete(ev.id)}>
                    Delete event
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {registrantEvent && (
        <div className="modal-overlay" onClick={() => setRegistrantEvent(null)}>
          <div className="modal-content-box" style={{ width: 'min(1000px, 94vw)' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem' }}>
              <div><h3>Event registrations</h3><p style={{ margin: '0.35rem 0 0', color: 'var(--text-secondary)' }}>{registrantEvent.title}</p></div>
              <button className="btn btn-outline" aria-label="Close registrations" onClick={() => setRegistrantEvent(null)}><X size={18} /></button>
            </div>
            {registrantsLoading ? <p>Loading registrations…</p> : registrantsError ? <p role="alert" style={{ color: 'var(--error)' }}>{registrantsError}</p> : registrants.length === 0 ? <p>No students have registered for this event yet.</p> : (
              <div className="admin-table-container" style={{ marginTop: '1rem' }}>
                <table className="admin-table">
                  <thead><tr><th>Student ID</th><th>Name</th><th>Email</th><th>Department</th><th>Year</th><th>Division</th><th>Registered</th></tr></thead>
                  <tbody>{registrants.map((student) => <tr key={student.registrationId}>
                    <td>{student.studentId || '—'}</td><td>{student.name || '—'}</td><td>{student.email || '—'}</td>
                    <td>{student.department || '—'}</td><td>{student.year || '—'}</td><td>{student.division || '—'}</td>
                    <td>{student.registeredAt ? new Date(student.registeredAt).toLocaleString() : '—'}</td>
                  </tr>)}</tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content-box">
            <h3>Publish Campus Event</h3>
            <form onSubmit={handleCreate} style={{ marginTop: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Event Title</label>
                <input type="text" className="form-input" placeholder="e.g. Annual Tech Symposium 2026" value={title} onChange={(e) => setTitle(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Organizer</label>
                <input type="text" className="form-input" placeholder="e.g. College Administration / Coding Club" value={organizer} onChange={(e) => setOrganizer(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Date</label>
                <input type="text" className="form-input" placeholder="e.g. October 10, 2026" value={date} onChange={(e) => setDate(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Venue</label>
                <input type="text" className="form-input" placeholder="e.g. Main Auditorium" value={venue} onChange={(e) => setVenue(e.target.value)} required />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Publish Event</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
