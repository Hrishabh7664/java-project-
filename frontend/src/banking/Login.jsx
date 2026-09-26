import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { login } from '../services/apiService';

const Login = () => {
  const [username, setUsername] = useState('student');
  const [password, setPassword] = useState('password123');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  const redirectPath = location.state?.from?.pathname || '/banking';

  const handleSubmit = async (e) => {
    e.preventDefault(); // Controlled form submit prevention
    setErrorMessage('');
    setLoading(true);

    try {
      await login(username, password);
      // Navigate to protected banking dashboard upon successful token issue
      navigate(redirectPath, { replace: true });
    } catch (err) {
      console.error('Authentication error:', err);
      const msg = err.response?.data?.message || err.message || 'Invalid username or password.';
      setErrorMessage(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleQuickFill = (user, pass) => {
    setUsername(user);
    setPassword(pass);
    setErrorMessage('');
  };

  return (
    <div
      className="d-flex align-items-center justify-content-center min-vh-100"
      style={{ backgroundColor: '#0f172a', padding: '1.5rem' }}
    >
      <div className="card border-0 shadow-lg p-4 p-sm-5" style={{ maxWidth: '440px', width: '100%', borderRadius: '16px' }}>
        <div className="text-center mb-4">
          <div
            className="d-inline-flex align-items-center justify-content-center rounded-circle bg-primary-subtle text-primary mb-3"
            style={{ width: '64px', height: '64px', fontSize: '1.8rem' }}
          >
            <i className="bi bi-shield-lock-fill"></i>
          </div>
          <h3 className="fw-bold mb-1 text-dark">Portal Authentication</h3>
          <p className="text-muted small mb-0">
            FSJP Experiment 06 &mdash; Stateless JWT Security
          </p>
        </div>

        {errorMessage && (
          <div className="alert alert-danger py-2 small mb-3" role="alert">
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label htmlFor="usernameInput" className="form-label small fw-semibold text-secondary">
              Username or College Email
            </label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-person"></i></span>
              <input
                id="usernameInput"
                type="text"
                className="form-control"
                placeholder="e.g. student or hrishabh"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={loading}
              />
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="passwordInput" className="form-label small fw-semibold text-secondary">
              Password
            </label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-lock"></i></span>
              <input
                id="passwordInput"
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary w-100 py-2 rounded-pill fw-semibold shadow-sm mb-3"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                Authenticating Token...
              </>
            ) : (
              <>
                <i className="bi bi-box-arrow-in-right me-1"></i> Sign In with JWT
              </>
            )}
          </button>
        </form>

        {/* Quick Credentials Helpers for Viva and Lab Evaluation */}
        <div className="border-top pt-3 mt-2">
          <span className="small text-muted d-block mb-2 text-uppercase fw-semibold" style={{ fontSize: '0.72rem' }}>
            Lab Evaluation Pre-seeded Accounts:
          </span>
          <div className="d-flex gap-2">
            <button
              type="button"
              className="btn btn-outline-secondary btn-sm flex-grow-1 text-nowrap"
              onClick={() => handleQuickFill('student', 'password123')}
            >
              <i className="bi bi-person-fill me-1"></i> Student
            </button>
            <button
              type="button"
              className="btn btn-outline-secondary btn-sm flex-grow-1 text-nowrap"
              onClick={() => handleQuickFill('admin', 'admin123')}
            >
              <i className="bi bi-shield-fill me-1"></i> Admin
            </button>
          </div>
        </div>

        <div className="text-center mt-4">
          <Link to="/" className="small text-decoration-none text-muted">
            <i className="bi bi-arrow-left me-1"></i> Return to Campus Connect Hub
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
