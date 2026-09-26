import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { fetchComplaints, createComplaint } from '../services/apiService';
import { useAuth } from '../context/AuthContext';

const ComplaintForm = () => {
  const { user } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [alert, setAlert] = useState(null);

  // Controlled form state
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('Infrastructure');
  const [description, setDescription] = useState('');
  const [anonymous, setAnonymous] = useState(false);

  const loadComplaints = async () => {
    try {
      setLoading(true);
      const data = await fetchComplaints();
      setComplaints(data);
    } catch (err) {
      console.error('Error fetching complaints:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadComplaints();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault(); // Controlled submit handling
    if (!title.trim() || !description.trim()) {
      setAlert({ message: 'Please provide both grievance subject and details.', type: 'danger' });
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        title: title.trim(),
        category,
        description: description.trim(),
        submittedBy: anonymous ? 'Anonymous Student' : (user?.username || 'Student User'),
        anonymous,
        status: 'IN_PROGRESS'
      };

      const created = await createComplaint(payload);
      setComplaints((prev) => [created, ...prev]);
      setAlert({ message: 'Grievance ticket logged securely with administration.', type: 'success' });
      setTitle('');
      setDescription('');
      setAnonymous(false);
      setTimeout(() => setAlert(null), 4000);
    } catch (err) {
      console.error('Error submitting complaint:', err);
      setAlert({ message: 'Failed to file grievance. Please try again.', type: 'danger' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-shield-exclamation text-danger me-2"></i> Grievance Redressal &amp; Moderation
            </h3>
            <p className="text-muted small mb-0">Confidential channel to report infrastructure bugs, academic conflicts, or security concerns.</p>
          </div>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        <div className="row g-4">
          {/* Submission Form Column */}
          <div className="col-12 col-lg-5">
            <div className="card shadow-sm border-0 p-4 h-100" style={{ borderRadius: '12px' }}>
              <h5 className="fw-bold mb-3 text-dark">
                <i className="bi bi-pencil-fill text-danger me-2"></i> File a Grievance Ticket
              </h5>

              <form onSubmit={handleSubmit} noValidate>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Subject / Title</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Lab 3 WiFi disconnection"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Issue Category</label>
                  <select
                    className="form-select"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    disabled={isSubmitting}
                  >
                    <option value="Infrastructure">Campus Infrastructure &amp; Labs</option>
                    <option value="Academics">Academic Schedules &amp; Materials</option>
                    <option value="Cafeteria">Canteen &amp; Cleanliness</option>
                    <option value="Hostel">Hostel &amp; Transport</option>
                    <option value="Security">Security &amp; Ragging Prevention</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Detailed Description</label>
                  <textarea
                    className="form-control"
                    rows="4"
                    placeholder="Explain the issue clearly with location and timing..."
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    disabled={isSubmitting}
                    required
                  ></textarea>
                </div>

                <div className="form-check mb-4">
                  <input
                    className="form-check-input"
                    type="checkbox"
                    id="anonCheck"
                    checked={anonymous}
                    onChange={(e) => setAnonymous(e.target.checked)}
                    disabled={isSubmitting}
                  />
                  <label className="form-check-label small text-secondary" htmlFor="anonCheck">
                    Submit Anonymously (Hide student profile ID)
                  </label>
                </div>

                <button type="submit" className="btn btn-danger w-100 py-2 rounded-pill fw-semibold shadow-sm" disabled={isSubmitting}>
                  {isSubmitting ? 'Logging Ticket...' : 'Submit Grievance'}
                </button>
              </form>
            </div>
          </div>

          {/* Active Grievances Ledger Column */}
          <div className="col-12 col-lg-7">
            <div className="card shadow-sm border-0 p-4 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold mb-0 text-dark">
                  <i className="bi bi-clock-history text-secondary me-2"></i> Logged Grievance Pipeline
                </h5>
                <span className="badge bg-secondary-subtle text-secondary">{complaints.length} Records</span>
              </div>

              {loading ? (
                <div className="text-center py-4">
                  <div className="spinner-border text-danger" role="status"></div>
                </div>
              ) : complaints.length > 0 ? (
                <div className="d-flex flex-column gap-3">
                  {complaints.map((c) => (
                    <div key={c.id} className="p-3 border rounded-3 bg-white">
                      <div className="d-flex justify-content-between align-items-start mb-2">
                        <span className="badge bg-light text-dark border">
                          {c.category || 'General'}
                        </span>
                        <span className={`badge ${c.status === 'RESOLVED' ? 'bg-success' : 'bg-warning text-dark'}`}>
                          {c.status || 'IN_PROGRESS'}
                        </span>
                      </div>
                      <h6 className="fw-bold text-dark mb-1">{c.title}</h6>
                      <p className="text-muted small mb-2">{c.description}</p>
                      <small className="text-secondary">
                        <i className="bi bi-person me-1"></i> Filed by: <strong>{c.submittedBy || 'Student'}</strong>
                      </small>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted text-center py-4 mb-0">No active grievances logged.</p>
              )}
            </div>
          </div>
        </div>
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default ComplaintForm;
