import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from './Navbar';
import { fetchStudents, fetchEvents, fetchClubs, fetchPosts, fetchNotices } from '../services/apiService';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
  const { user } = useAuth();
  const [counts, setCounts] = useState({
    students: 0,
    clubs: 0,
    events: 0,
    posts: 0
  });
  const [notices, setNotices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);
        const [students, clubs, events, posts, noticesData] = await Promise.all([
          fetchStudents().catch(() => []),
          fetchClubs().catch(() => []),
          fetchEvents().catch(() => []),
          fetchPosts().catch(() => []),
          fetchNotices().catch(() => [])
        ]);

        setCounts({
          students: students.length,
          clubs: clubs.length,
          events: events.length,
          posts: posts.length
        });
        setNotices(noticesData.slice(0, 5));
      } catch (err) {
        console.error('Error loading dashboard stats:', err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        {/* Welcome Banner */}
        <div className="card shadow-sm border-0 p-4 mb-4 bg-primary text-white" style={{ borderRadius: '14px' }}>
          <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
            <div>
              <h2 className="fw-bold mb-1">Welcome, {user?.username || 'Student'}! 👋</h2>
              <p className="mb-0 text-white-50">
                Thakur Shree DPS College of Engineering and Management &mdash; Digital Campus Ecosystem
              </p>
            </div>
            <div className="d-flex gap-2">
              <Link to="/posts" className="btn btn-light rounded-pill px-3 fw-semibold">
                <i className="bi bi-pencil-square me-1"></i> New Post
              </Link>
              <Link to="/events" className="btn btn-outline-light rounded-pill px-3">
                <i className="bi bi-calendar-plus me-1"></i> View Events
              </Link>
            </div>
          </div>
        </div>

        {/* 4 Summary Cards (Responsive Grid: col-12 col-sm-6 col-lg-3) */}
        <div className="row g-3 mb-4">
          <div className="col-12 col-sm-6 col-lg-3">
            <div className="card shadow-sm border-0 p-3 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <span className="text-muted small fw-semibold text-uppercase">Total Students</span>
                  <h3 className="fw-bold mb-0 text-dark">{loading ? '...' : counts.students}</h3>
                </div>
                <div className="p-3 bg-primary-subtle text-primary rounded-3">
                  <i className="bi bi-people-fill fs-4"></i>
                </div>
              </div>
              <small className="text-success mt-2 d-block"><i className="bi bi-check2"></i> Registered Profiles</small>
            </div>
          </div>

          <div className="col-12 col-sm-6 col-lg-3">
            <div className="card shadow-sm border-0 p-3 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <span className="text-muted small fw-semibold text-uppercase">Active Clubs</span>
                  <h3 className="fw-bold mb-0 text-success">{loading ? '...' : counts.clubs}</h3>
                </div>
                <div className="p-3 bg-success-subtle text-success rounded-3">
                  <i className="bi bi-award-fill fs-4"></i>
                </div>
              </div>
              <small className="text-muted mt-2 d-block"><i className="bi bi-lightning-charge"></i> Student Organizations</small>
            </div>
          </div>

          <div className="col-12 col-sm-6 col-lg-3">
            <div className="card shadow-sm border-0 p-3 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <span className="text-muted small fw-semibold text-uppercase">Upcoming Events</span>
                  <h3 className="fw-bold mb-0 text-warning">{loading ? '...' : counts.events}</h3>
                </div>
                <div className="p-3 bg-warning-subtle text-warning rounded-3">
                  <i className="bi bi-calendar-event-fill fs-4"></i>
                </div>
              </div>
              <small className="text-muted mt-2 d-block"><i className="bi bi-ticket-perforated"></i> Hackathons &amp; Fests</small>
            </div>
          </div>

          <div className="col-12 col-sm-6 col-lg-3">
            <div className="card shadow-sm border-0 p-3 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <span className="text-muted small fw-semibold text-uppercase">Discussion Posts</span>
                  <h3 className="fw-bold mb-0 text-info">{loading ? '...' : counts.posts}</h3>
                </div>
                <div className="p-3 bg-info-subtle text-info rounded-3">
                  <i className="bi bi-chat-dots-fill fs-4"></i>
                </div>
              </div>
              <small className="text-muted mt-2 d-block"><i className="bi bi-fire"></i> Campus Social Hub</small>
            </div>
          </div>
        </div>

        {/* Content Row: Official Circulars & Quick Shortcuts */}
        <div className="row g-4">
          <div className="col-12 col-lg-8">
            <div className="card shadow-sm border-0 p-4 h-100" style={{ borderRadius: '12px' }}>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold mb-0 text-dark">
                  <i className="bi bi-megaphone-fill text-primary me-2"></i> Official Notice Board
                </h5>
                <span className="badge bg-secondary-subtle text-secondary">Verified Circulars</span>
              </div>
              <div className="list-group list-group-flush">
                {notices.length > 0 ? (
                  notices.map((n) => (
                    <div key={n.id} className="list-group-item px-0 py-3">
                      <div className="d-flex justify-content-between align-items-start">
                        <div>
                          <h6 className="fw-bold mb-1 text-dark">{n.title}</h6>
                          <p className="text-muted small mb-1">{n.content}</p>
                          <small className="text-secondary">
                            <i className="bi bi-person me-1"></i> {n.author} &middot; <span className="badge bg-light text-dark border">{n.category}</span>
                          </small>
                        </div>
                        <small className="text-muted text-nowrap">{n.publishedAt || 'Today'}</small>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="text-muted small my-3">No active circulars at this moment.</p>
                )}
              </div>
            </div>
          </div>

          <div className="col-12 col-lg-4">
            <div className="card shadow-sm border-0 p-4 h-100" style={{ borderRadius: '12px' }}>
              <h5 className="fw-bold mb-3 text-dark">
                <i className="bi bi-compass-fill text-primary me-2"></i> Campus Navigation
              </h5>
              <div className="d-grid gap-2">
                <Link to="/posts" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-chat-square-text me-2"></i> Campus Discussions</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/events" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-calendar-event me-2"></i> Event Registrations</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/clubs" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-people me-2"></i> College Clubs &amp; Teams</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/assignments" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-journal-check me-2"></i> Academic Assignments</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/elections" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-check2-square me-2"></i> Student Elections</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/complaints" className="btn btn-outline-primary text-start py-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-shield-exclamation me-2"></i> Student Grievances</span>
                  <i className="bi bi-chevron-right small"></i>
                </Link>
                <Link to="/banking" className="btn btn-primary text-start py-2 d-flex align-items-center justify-content-between mt-2">
                  <span><i className="bi bi-bank2 me-2"></i> Banking Portal (Exp 2 &amp; 3)</span>
                  <i className="bi bi-arrow-right small"></i>
                </Link>
              </div>
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

export default Dashboard;
