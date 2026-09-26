import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [username, setUsername] = useState('student');
  const [password, setPassword] = useState('password123');
  const [error, setError] = useState('');
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const redirectPath = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault(); // Controlled submit handling
    setError('');

    if (!username.trim() || !password.trim()) {
      setError('Please provide both username and password.');
      return;
    }

    try {
      await login(username, password);
      navigate(redirectPath, { replace: true });
    } catch (err) {
      console.error('Login error:', err);
      const msg = err.response?.data?.message || err.message || 'Invalid username or password.';
      setError(msg);
    }
  };

  const handleQuickEnter = (u, p) => {
    setUsername(u);
    setPassword(p);
    setError('');
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 bg-light py-5">
      <div className="card shadow-lg border-0 p-4 p-md-5" style={{ maxWidth: '440px', width: '100%', borderRadius: '16px' }}>
        <div className="text-center mb-4">
          <div className="d-inline-flex p-3 rounded-circle bg-primary-subtle text-primary mb-3">
            <i className="bi bi-mortarboard-fill fs-2"></i>
          </div>
          <h3 className="fw-bold text-dark mb-1">Campus Connect</h3>
          <p className="text-muted small">Sign in to your college account</p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 small mb-3" role="alert">
            <i className="bi bi-exclamation-triangle-fill me-2"></i> {error}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label className="form-label small fw-semibold text-secondary">Username or Email</label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-person"></i></span>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. student or hrishabh"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label className="form-label small fw-semibold text-secondary">Password</label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-lock"></i></span>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
                required
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
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </button>
        </form>

        <div className="border-top pt-3 mt-2 text-center">
          <span className="small text-muted d-block mb-2 text-uppercase fw-semibold" style={{ fontSize: '0.72rem' }}>
            Quick 1-Click Evaluation Accounts
          </span>
          <div className="d-flex gap-2">
            <button
              type="button"
              className="btn btn-outline-secondary btn-sm flex-grow-1"
              onClick={() => handleQuickEnter('student', 'password123')}
            >
              <i className="bi bi-person-fill me-1"></i> Student
            </button>
            <button
              type="button"
              className="btn btn-outline-secondary btn-sm flex-grow-1"
              onClick={() => handleQuickEnter('admin', 'admin123')}
            >
              <i className="bi bi-shield-fill me-1"></i> Admin
            </button>
          </div>
        </div>

        <div className="text-center mt-4">
          <Link to="/banking" className="small text-decoration-none text-muted">
            <i className="bi bi-bank2 me-1"></i> Go to Banking Portal (Exp 2 &amp; 3)
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
