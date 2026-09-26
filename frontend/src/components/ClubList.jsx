import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import ClubForm from './ClubForm';
import { fetchClubs, createClub } from '../services/apiService';

const ClubList = () => {
  const [clubs, setClubs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [joinedClubs, setJoinedClubs] = useState({});
  const [alert, setAlert] = useState(null);

  const loadClubs = async () => {
    try {
      setLoading(true);
      const data = await fetchClubs();
      setClubs(data);
    } catch (err) {
      console.error('Error fetching clubs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadClubs();
  }, []);

  const handleClubCreated = async (clubData, onSuccess) => {
    setIsSubmitting(true);
    try {
      const created = await createClub(clubData);
      setClubs((prev) => [created, ...prev]);
      setAlert({ message: `Club "${created.name}" chartered successfully!`, type: 'success' });
      if (onSuccess) onSuccess();
      setTimeout(() => setAlert(null), 4000);
    } catch (err) {
      console.error('Error creating club:', err);
      setAlert({ message: 'Failed to create club.', type: 'danger' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleJoin = (clubId, clubName) => {
    setJoinedClubs((prev) => {
      const newState = !prev[clubId];
      setAlert({
        message: newState
          ? `You have joined ${clubName}!`
          : `Membership left for ${clubName}.`,
        type: newState ? 'success' : 'info'
      });
      setTimeout(() => setAlert(null), 3000);
      return { ...prev, [clubId]: newState };
    });
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-people text-primary me-2"></i> Student Clubs &amp; Committees
            </h3>
            <p className="text-muted small mb-0">Join institutional organizations, participate in projects, and assume leadership roles.</p>
          </div>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        <ClubForm onClubCreated={handleClubCreated} isSubmitting={isSubmitting} />

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status"></div>
          </div>
        ) : clubs.length > 0 ? (
          <div className="row g-4">
            {clubs.map((club) => {
              const isJoined = joinedClubs[club.id];
              return (
                <div key={club.id} className="col-12 col-md-6 col-lg-4">
                  <div className="card shadow-sm border-0 h-100 p-4 d-flex flex-column" style={{ borderRadius: '12px' }}>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="badge bg-success-subtle text-success border border-success-subtle">
                        {club.category || 'General'}
                      </span>
                      <span className="small text-muted">
                        <i className="bi bi-building me-1"></i> {club.department || 'Central'}
                      </span>
                    </div>

                    <h5 className="fw-bold text-dark mb-2">{club.name}</h5>
                    <p className="text-muted small flex-grow-1">{club.description}</p>

                    <div className="border-top pt-3 mt-3">
                      <div className="small text-secondary mb-1">
                        <i className="bi bi-person-badge me-1 text-primary"></i> <strong>President:</strong> {club.president || 'Elected Lead'}
                      </div>
                      <div className="small text-secondary mb-3">
                        <i className="bi bi-people me-1 text-success"></i> <strong>Roster:</strong> {(club.membersCount || 20) + (isJoined ? 1 : 0)} Active Members
                      </div>

                      <button
                        type="button"
                        className={`btn w-100 rounded-pill btn-sm fw-semibold ${isJoined ? 'btn-success' : 'btn-outline-primary'}`}
                        onClick={() => handleToggleJoin(club.id, club.name)}
                      >
                        {isJoined ? (
                          <>
                            <i className="bi bi-check-circle-fill me-1"></i> Member Active
                          </>
                        ) : (
                          <>
                            <i className="bi bi-person-plus me-1"></i> Join Organization
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
            <p className="mb-0">No clubs registered yet.</p>
          </div>
        )}
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default ClubList;
