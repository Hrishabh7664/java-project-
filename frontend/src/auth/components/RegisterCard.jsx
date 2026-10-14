import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LockKeyhole, UserRoundPlus } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { apiErrorMessage } from '../../services/api';

export const RegisterCard = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', department: '', year: '', division: '', batch: 'A1' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const update = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const user = await register(form);
      navigate(user.role === 'STUDENT' ? '/student' : '/login', { replace: true });
    } catch (requestError) {
      setError(apiErrorMessage(requestError, requestError.message || 'Registration failed.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-card">
      <div className="auth-header">
        <h2 className="auth-title">Join Campus Connect</h2>
        <p className="auth-subtitle">Create your student account to connect with campus</p>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label" htmlFor="name">Full name</label>
          <input className="form-input" id="name" name="name" autoComplete="name" value={form.name} onChange={update} maxLength="120" required />
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="email">College email</label>
          <input className="form-input" id="email" name="email" type="email" placeholder="you@tsdcem.ac.in" autoComplete="email" value={form.email} onChange={update} required />
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="password">Password (at least 8 characters)</label>
          <div className="input-wrapper">
            <LockKeyhole className="input-icon" size={18} />
            <input className="form-input" id="password" name="password" type="password" autoComplete="new-password" minLength="8" maxLength="72" value={form.password} onChange={update} required />
          </div>
        </div>
        <div className="auth-form-grid">
          <div className="form-group">
            <label className="form-label" htmlFor="department">Department</label>
            <select className="form-input" id="department" name="department" value={form.department} onChange={update} required>
              <option value="">Select</option><option value="IT">Information Technology (Demo)</option><option value="CE">Computer Engineering (Demo)</option><option value="ECS">Electronics and Computer Science (Demo)</option><option value="ME">Mechanical Engineering (Demo)</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="year">Year</label>
            <select className="form-input" id="year" name="year" value={form.year} onChange={update} required>
              <option value="">Select</option><option value="FE">First Year Engineering (FE)</option><option value="SE">Second Year Engineering (SE)</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="division">Division</label>
            <select className="form-input" id="division" name="division" value={form.division} onChange={update} required>
              <option value="">Select</option><option value="A">Division A (Demo)</option>
            </select>
          </div>
          <div className="form-group"><label className="form-label" htmlFor="batch">Practical batch</label>
            <select className="form-input" id="batch" name="batch" value={form.batch} onChange={update} required><option value="A1">Batch A1 (Demo)</option></select>
          </div>
        </div>
        {error && <div className="validation-error" role="alert">{error}</div>}
        <button type="submit" className="btn btn-primary submit-btn" disabled={submitting}>
          <UserRoundPlus size={18} /> {submitting ? 'Creating account…' : 'Create student account'}
        </button>
      </form>
      <p className="auth-footer-text">Already registered? <Link to="/login">Sign in</Link></p>
    </div>
  );
};
