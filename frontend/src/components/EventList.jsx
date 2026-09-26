import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import EventForm from './EventForm';
import { fetchEvents, createEvent } from '../services/apiService';

const EventList = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [registeredEvents, setRegisteredEvents] = useState({});
  const [alert, setAlert] = useState(null);

  const loadEvents = async () => {
    try {
      setLoading(true);
      const data = await fetchEvents();
      setEvents(data);
    } catch (err) {
      console.error('Error fetching events:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEvents();
  }, []);

  const handleEventCreated = async (eventData, onSuccess) => {
    setIsSubmitting(true);
    try {
      const created = await createEvent(eventData);
      setEvents((prev) => [created, ...prev]);
      setAlert({ message: `Event "${created.title}" registered successfully!`, type: 'success' });
      if (onSuccess) onSuccess();
      setTimeout(() => setAlert(null), 4000);
    } catch (err) {
      console.error('Error creating event:', err);
      setAlert({ message: 'Failed to create event.', type: 'danger' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleRsvp = (eventId, eventTitle) => {
    setRegisteredEvents((prev) => {
      const newState = !prev[eventId];
      setAlert({
        message: newState
          ? `RSVP Confirmed for ${eventTitle}!`
          : `Registration cancelled for ${eventTitle}.`,
        type: newState ? 'success' : 'info'
      });
      setTimeout(() => setAlert(null), 3000);
      return { ...prev, [eventId]: newState };
    });
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-calendar-event text-primary me-2"></i> Campus Events &amp; Workshops
            </h3>
            <p className="text-muted small mb-0">Hackathons, cultural fests, academic seminars, and guest lectures.</p>
          </div>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        <EventForm onEventCreated={handleEventCreated} isSubmitting={isSubmitting} />

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status"></div>
          </div>
        ) : events.length > 0 ? (
          <div className="row g-4">
            {events.map((evt) => {
              const isRsvp = registeredEvents[evt.id];
              return (
                <div key={evt.id} className="col-12 col-md-6 col-lg-4">
                  <div className="card shadow-sm border-0 h-100 d-flex flex-column" style={{ borderRadius: '12px' }}>
                    <div className="card-body p-4 d-flex flex-column">
                      <div className="d-flex justify-content-between align-items-start mb-2">
                        <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                          {evt.category || 'Workshop'}
                        </span>
                        <small className="text-muted fw-semibold">
                          <i className="bi bi-calendar3 me-1"></i> {evt.date}
                        </small>
                      </div>

                      <h5 className="card-title fw-bold text-dark mb-2">{evt.title}</h5>
                      <p className="card-text text-muted small flex-grow-1">{evt.description}</p>

                      <div className="border-top pt-3 mt-3">
                        <div className="small text-secondary mb-1">
                          <i className="bi bi-geo-alt me-1 text-danger"></i> <strong>Venue:</strong> {evt.location}
                        </div>
                        <div className="small text-secondary mb-3">
                          <i className="bi bi-clock me-1 text-primary"></i> <strong>Time:</strong> {evt.time} &middot; By {evt.organizer}
                        </div>

                        <button
                          type="button"
                          className={`btn w-100 rounded-pill btn-sm fw-semibold ${isRsvp ? 'btn-success' : 'btn-outline-primary'}`}
                          onClick={() => handleToggleRsvp(evt.id, evt.title)}
                        >
                          {isRsvp ? (
                            <>
                              <i className="bi bi-check2-circle me-1"></i> Registered &middot; Ticket Confirmed
                            </>
                          ) : (
                            <>
                              <i className="bi bi-ticket-perforated me-1"></i> RSVP / Register Now
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="card shadow-sm border-0 p-5 text-center text-muted" style={{ borderRadius: '12px' }}>
            <p className="mb-0">No events currently scheduled.</p>
          </div>
        )}
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default EventList;
