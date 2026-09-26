import React, { useState } from 'react';

const ClubForm = ({ onClubCreated, isSubmitting }) => {
  const [name, setName] = useState('');
  const [category, setCategory] = useState('Technology');
  const [department, setDepartment] = useState('IT');
  const [president, setPresident] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!name.trim() || !president.trim()) {
      setError('Please provide club name and designated president.');
      return;
    }

    onClubCreated(
      {
        name: name.trim(),
        category,
        department,
        president: president.trim(),
        description: description.trim() || 'Official student organization.',
        membersCount: 1
      },
      () => {
        setName('');
        setPresident('');
        setDescription('');
      }
    );
  };

  return (
    <div className="card shadow-sm border-0 p-4 mb-4" style={{ borderRadius: '12px' }}>
      <h5 className="fw-bold mb-3 text-dark">
        <i className="bi bi-plus-circle text-primary me-2"></i> Register New Student Club / Committee
      </h5>

      {error && <div className="alert alert-danger py-2 small mb-3">{error}</div>}

      <form onSubmit={handleSubmit} noValidate>
        <div className="row g-3 mb-3">
          <div className="col-12 col-md-6">
            <label className="form-label small fw-semibold">Club / Organization Name</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. AI &amp; Robotics Guild"
              value={name}
              onChange={(e) => setName(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-3">
            <label className="form-label small fw-semibold">Domain / Category</label>
            <select
              className="form-select"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              disabled={isSubmitting}
            >
              <option value="Technology">Technology</option>
              <option value="Engineering">Engineering</option>
              <option value="Arts &amp; Culture">Arts &amp; Culture</option>
              <option value="Sports">Sports &amp; Fitness</option>
              <option value="Social Service">Social Service</option>
            </select>
          </div>
          <div className="col-12 col-md-3">
            <label className="form-label small fw-semibold">Affiliated Department</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. IT, EXTC, Central"
              value={department}
              onChange={(e) => setDepartment(e.target.value)}
              disabled={isSubmitting}
            />
          </div>
        </div>

        <div className="row g-3 mb-3">
          <div className="col-12 col-md-6">
            <label className="form-label small fw-semibold">Designated Student President / Lead</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Anurag Yadav"
              value={president}
              onChange={(e) => setPresident(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-6">
            <label className="form-label small fw-semibold">Mission Statement / Description</label>
            <input
              type="text"
              className="form-control"
              placeholder="Purpose and goals of this club..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              disabled={isSubmitting}
            />
          </div>
        </div>

        <div className="d-flex justify-content-end">
          <button type="submit" className="btn btn-primary rounded-pill px-4" disabled={isSubmitting}>
            {isSubmitting ? 'Registering...' : 'Charter Club'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ClubForm;
