import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { fetchAssignments, createAssignment } from '../services/apiService';
import { useAuth } from '../context/AuthContext';

const AssignmentList = () => {
  const { user } = useAuth();
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [submittedTasks, setSubmittedTasks] = useState({});
  const [alert, setAlert] = useState(null);

  // Controlled form state
  const [title, setTitle] = useState('');
  const [subject, setSubject] = useState('Full Stack Java Programming');
  const [facultyName, setFacultyName] = useState('Prof. Sumeet Rathod');
  const [dueDate, setDueDate] = useState('2026-10-15');
  const [description, setDescription] = useState('');

  const loadAssignments = async () => {
    try {
      setLoading(true);
      const data = await fetchAssignments();
      setAssignments(data);
    } catch (err) {
      console.error('Error fetching assignments:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAssignments();
  }, []);

  const handleCreateAssignment = async (e) => {
    e.preventDefault(); // Controlled submit handling
    if (!title.trim() || !description.trim()) {
      setAlert({ message: 'Please provide assignment title and instructions.', type: 'danger' });
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        title: title.trim(),
        subject,
        facultyName,
        dueDate,
        description: description.trim(),
        status: 'PENDING'
      };
      const created = await createAssignment(payload);
      setAssignments((prev) => [created, ...prev]);
      setAlert({ message: `Assignment "${created.title}" published!`, type: 'success' });
      setTitle('');
      setDescription('');
      setShowForm(false);
      setTimeout(() => setAlert(null), 4000);
    } catch (err) {
      console.error('Error creating assignment:', err);
      setAlert({ message: 'Failed to create assignment.', type: 'danger' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleSubmit = (id, taskTitle) => {
    setSubmittedTasks((prev) => {
      const isDone = !prev[id];
      setAlert({
        message: isDone
          ? `Your solution for "${taskTitle}" was submitted for evaluation!`
          : `Submission recalled for "${taskTitle}".`,
        type: isDone ? 'success' : 'info'
      });
      setTimeout(() => setAlert(null), 3000);
      return { ...prev, [id]: isDone };
    });
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-journal-bookmark text-primary me-2"></i> Academic Assignments &amp; Projects
            </h3>
            <p className="text-muted small mb-0">Course assignments, deadlines, submission management, and faculty evaluations.</p>
          </div>

          <button
            type="button"
            className="btn btn-primary rounded-pill px-4"
            onClick={() => setShowForm(!showForm)}
          >
            <i className={`bi ${showForm ? 'bi-x-circle' : 'bi-plus-circle'} me-1`}></i>
            {showForm ? 'Close Form' : 'Publish Assignment'}
          </button>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        {/* Faculty Assignment Creation Form */}
        {showForm && (
          <div className="card shadow-sm border-0 p-4 mb-4" style={{ borderRadius: '12px' }}>
            <h5 className="fw-bold mb-3 text-dark">
              <i className="bi bi-file-earmark-plus text-primary me-2"></i> Create Course Assignment
            </h5>

            <form onSubmit={handleCreateAssignment} noValidate>
              <div className="row g-3 mb-3">
                <div className="col-12 col-md-6">
                  <label className="form-label small fw-semibold">Assignment Title</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. Experiment 06 - JWT Authentication"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>
                <div className="col-12 col-md-6">
                  <label className="form-label small fw-semibold">Course / Subject</label>
                  <input
                    type="text"
                    className="form-control"
                    value={subject}
                    onChange={(e) => setSubject(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>

              <div className="row g-3 mb-3">
                <div className="col-12 col-md-6">
                  <label className="form-label small fw-semibold">Faculty In-Charge</label>
                  <input
                    type="text"
                    className="form-control"
                    value={facultyName}
                    onChange={(e) => setFacultyName(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>
                <div className="col-12 col-md-6">
                  <label className="form-label small fw-semibold">Submission Deadline</label>
                  <input
                    type="date"
                    className="form-control"
                    value={dueDate}
                    onChange={(e) => setDueDate(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Instructions &amp; Problem Statement</label>
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Provide problem specifications, repository submission link guidelines..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  disabled={isSubmitting}
                  required
                ></textarea>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button
                  type="button"
                  className="btn btn-outline-secondary rounded-pill px-4"
                  onClick={() => setShowForm(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary rounded-pill px-4" disabled={isSubmitting}>
                  {isSubmitting ? 'Publishing...' : 'Create Assignment'}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Assignments Table & Cards */}
        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status"></div>
          </div>
        ) : assignments.length > 0 ? (
          <div className="row g-3">
            {assignments.map((task) => {
              const isSubmitted = submittedTasks[task.id];
              return (
                <div key={task.id} className="col-12">
                  <div className="card shadow-sm border-0 p-4" style={{ borderRadius: '12px' }}>
                    <div className="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                      <div>
                        <span className="badge bg-primary-subtle text-primary border border-primary-subtle me-2">
                          {task.subject}
                        </span>
                        <span className={`badge ${isSubmitted ? 'bg-success' : 'bg-warning text-dark'}`}>
                          {isSubmitted ? 'SUBMITTED' : (task.status || 'PENDING')}
                        </span>
                      </div>
                      <small className="text-danger fw-semibold">
                        <i className="bi bi-clock-history me-1"></i> Due: {task.dueDate}
                      </small>
                    </div>

                    <h5 className="fw-bold text-dark mb-2">{task.title}</h5>
                    <p className="text-muted small mb-3">{task.description}</p>

                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-top pt-3">
                      <small className="text-secondary">
                        <i className="bi bi-person-workspace me-1"></i> Instructor: <strong>{task.facultyName}</strong>
                      </small>

                      <button
                        type="button"
                        className={`btn btn-sm rounded-pill px-4 ${isSubmitted ? 'btn-outline-danger' : 'btn-success'}`}
                        onClick={() => handleToggleSubmit(task.id, task.title)}
                      >
                        {isSubmitted ? (
                          <>
                            <i className="bi bi-arrow-counterclockwise me-1"></i> Recall Submission
                          </>
                        ) : (
                          <>
                            <i className="bi bi-upload me-1"></i> Turn In Assignment
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="card shadow-sm border-0 p-5 text-center text-muted" style={{ borderRadius: '12px' }}>
            <p className="mb-0">No assignments active at the moment.</p>
          </div>
        )}
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default AssignmentList;
