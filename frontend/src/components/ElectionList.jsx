import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { fetchElections } from '../services/apiService';

const ElectionList = () => {
  const [elections, setElections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [votes, setVotes] = useState({});
  const [alert, setAlert] = useState(null);

  useEffect(() => {
    const loadElections = async () => {
      try {
        setLoading(true);
        const data = await fetchElections();
        setElections(data);
      } catch (err) {
        console.error('Error fetching elections:', err);
      } finally {
        setLoading(false);
      }
    };

    loadElections();
  }, []);

  const handleCastVote = (electionId, candidateName, position) => {
    if (votes[electionId]) {
      setAlert({ message: `You have already cast your vote for ${position}.`, type: 'warning' });
      return;
    }

    setVotes((prev) => ({ ...prev, [electionId]: candidateName }));
    setAlert({
      message: `Your vote has been cast for ${candidateName} (${position})!`,
      type: 'success'
    });
    setTimeout(() => setAlert(null), 4000);
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-check2-square text-primary me-2"></i> Student Council &amp; Campus Elections
            </h3>
            <p className="text-muted small mb-0">Democratically elect student representatives, club presidents, and council officers.</p>
          </div>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status"></div>
          </div>
        ) : elections.length > 0 ? (
          <div className="row g-4">
            {elections.map((elec) => {
              const votedCandidate = votes[elec.id];
              return (
                <div key={elec.id} className="col-12 col-lg-6">
                  <div className="card shadow-sm border-0 p-4 h-100" style={{ borderRadius: '12px' }}>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                        {elec.position || 'Student Representative'}
                      </span>
                      <span className="badge bg-success">ACTIVE POLL</span>
                    </div>

                    <h5 className="fw-bold text-dark mb-1">{elec.title}</h5>
                    <p className="text-muted small mb-3">
                      <i className="bi bi-clock me-1"></i> Voting Window: {elec.startDate} to {elec.endDate}
                    </p>

                    <h6 className="small fw-bold text-uppercase text-secondary mb-3">Nominated Candidates:</h6>

                    <div className="list-group mb-3">
                      {['Anurag Yadav (IT Department)', 'Hrishabh Soni (AIDS Department)', 'Atul Tiwari (CE Department)'].map((cand, idx) => {
                        const isThisChosen = votedCandidate === cand;
                        return (
                          <div
                            key={idx}
                            className={`list-group-item d-flex justify-content-between align-items-center p-3 ${isThisChosen ? 'list-group-item-success border-success' : ''}`}
                          >
                            <div className="d-flex align-items-center gap-2">
                              <i className="bi bi-person-circle fs-5 text-secondary"></i>
                              <div>
                                <span className="fw-semibold text-dark">{cand}</span>
                                {isThisChosen && (
                                  <span className="badge bg-success ms-2">Your Vote</span>
                                )}
                              </div>
                            </div>

                            <button
                              type="button"
                              className={`btn btn-sm rounded-pill px-3 ${isThisChosen ? 'btn-success' : votedCandidate ? 'btn-light' : 'btn-outline-primary'}`}
                              disabled={!!votedCandidate}
                              onClick={() => handleCastVote(elec.id, cand, elec.position)}
                            >
                              {isThisChosen ? 'Voted' : 'Vote'}
                            </button>
                          </div>
                        );
                      })}
                    </div>

                    <small className="text-muted d-block text-center border-top pt-2">
                      <i className="bi bi-shield-lock me-1"></i> Secured by Single Student Vote Constraint
                    </small>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="card shadow-sm border-0 p-5 text-center text-muted" style={{ borderRadius: '12px' }}>
            <p className="mb-0">No elections currently active.</p>
          </div>
        )}
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default ElectionList;
