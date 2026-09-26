import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm py-2">
      <div className="container">
        <Link className="navbar-brand fw-bold d-flex align-items-center" to="/dashboard">
          <i className="bi bi-mortarboard-fill text-primary fs-4 me-2"></i>
          <span className="text-white">Campus<span className="text-primary">Connect</span></span>
        </Link>

        <button
          className="navbar-toggler border-0"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#campusNavbar"
          aria-controls="campusNavbar"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="campusNavbar">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/dashboard">
                <i className="bi bi-speedometer2 me-1"></i> Dashboard
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/posts">
                <i className="bi bi-chat-square-text me-1"></i> Social Hub
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/events">
                <i className="bi bi-calendar-event me-1"></i> Events
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/clubs">
                <i className="bi bi-people me-1"></i> Clubs
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/assignments">
                <i className="bi bi-journal-bookmark me-1"></i> Academics
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/elections">
                <i className="bi bi-check2-square me-1"></i> Elections
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link px-3 ${isActive ? 'active fw-semibold text-primary' : ''}`} to="/complaints">
                <i className="bi bi-exclamation-octagon me-1"></i> Grievances
              </NavLink>
            </li>
          </ul>

          <div className="d-flex align-items-center gap-2">
            <Link to="/banking" className="btn btn-outline-info btn-sm rounded-pill px-3">
              <i className="bi bi-bank2 me-1"></i> Banking Portal
            </Link>

            {isAuthenticated ? (
              <div className="dropdown">
                <button
                  className="btn btn-secondary btn-sm dropdown-toggle rounded-pill px-3 d-flex align-items-center gap-2"
                  type="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  <i className="bi bi-person-circle"></i>
                  <span>{user?.username || 'User'}</span>
                  <span className="badge bg-primary ms-1" style={{ fontSize: '0.65rem' }}>
                    {user?.role === 'ROLE_ADMIN' ? 'Admin' : 'Student'}
                  </span>
                </button>
                <ul className="dropdown-menu dropdown-menu-end shadow-sm">
                  <li><span className="dropdown-item-text small text-muted">Signed in as <strong>{user?.username}</strong></span></li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <button className="dropdown-item text-danger" onClick={handleLogout}>
                      <i className="bi bi-box-arrow-right me-2"></i> Log Out
                    </button>
                  </li>
                </ul>
              </div>
            ) : (
              <Link to="/login" className="btn btn-primary btn-sm rounded-pill px-4">
                Sign In
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
