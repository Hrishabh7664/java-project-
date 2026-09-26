import React, { useState } from 'react';

const EventForm = ({ onEventCreated, isSubmitting }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [organizer, setOrganizer] = useState('Coding Club');
  const [date, setDate] = useState('2026-10-25');
  const [time, setTime] = useState('10:00 AM');
  const [location, setLocation] = useState('Auditorium');
  const [category, setCategory] = useState('Technology');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!title.trim() || !description.trim()) {
      setError('Please provide event title and description.');
      return;
    }

    onEventCreated(
      { title: title.trim(), description: description.trim(), organizer, date, time, location, category },
      () => {
        setTitle('');
        setDescription('');
      }
    );
  };

  return (
    <div className="card shadow-sm border-0 p-4 mb-4" style={{ borderRadius: '12px' }}>
      <h5 className="fw-bold mb-3 text-dark">
        <i className="bi bi-calendar-plus text-primary me-2"></i> Register New Campus Event
      </h5>

      {error && <div className="alert alert-danger py-2 small mb-3">{error}</div>}

      <form onSubmit={handleSubmit} noValidate>
        <div className="row g-3 mb-3">
          <div className="col-12 col-md-6">
            <label className="form-label small fw-semibold">Event Name</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Annual Hackathon 2026"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-6">
            <label className="form-label small fw-semibold">Organizing Club / Committee</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Coding Club"
              value={organizer}
              onChange={(e) => setOrganizer(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
        </div>

        <div className="row g-3 mb-3">
          <div className="col-12 col-md-4">
            <label className="form-label small fw-semibold">Event Date</label>
            <input
              type="date"
              className="form-control"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-4">
            <label className="form-label small fw-semibold">Time</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. 09:30 AM"
              value={time}
              onChange={(e) => setTime(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-4">
            <label className="form-label small fw-semibold">Campus Venue</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Seminar Hall 2"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">Event Overview &amp; Schedule</label>
          <textarea
            className="form-control"
            rows="2"
            placeholder="Details, eligibility criteria, and guest speakers..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            disabled={isSubmitting}
            required
          ></textarea>
        </div>

        <div className="d-flex justify-content-end">
          <button type="submit" className="btn btn-primary rounded-pill px-4" disabled={isSubmitting}>
            {isSubmitting ? 'Publishing...' : 'Publish Event'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default EventForm;
