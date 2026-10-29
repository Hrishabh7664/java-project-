import React, { useState, useEffect } from 'react';
import { clubManagementService } from '../services/clubManagementService';
import { PlusCircle, Briefcase } from 'lucide-react';

export const ManageClubs = () => {
  const [clubs, setClubs] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('Technical');
  const [leader, setLeader] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [leaderEmails, setLeaderEmails] = useState({});
  const [savingLeader, setSavingLeader] = useState(null);

  useEffect(() => {
    async function load() {
      try { setClubs(await clubManagementService.getClubs()); } catch (e) { setError(e.response?.data?.message || 'Clubs could not be loaded.'); }
      finally { setLoading(false); }
    }
    load();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!name) return;
    try { await clubManagementService.createClub({ name, category, leaderEmail: leader });
    const updated = await clubManagementService.getClubs();
    setClubs(updated);
    setName('');
    setLeader('');
    setShowModal(false);
    setError(''); } catch (e) { setError(e.response?.data?.message || 'Club could not be created.'); }
  };

  const handleDelete = async (id) => {
    try { await clubManagementService.deleteClub(id); setError(''); }
    catch (e) { setError(e.response?.data?.message || 'Club could not be deleted.'); return; }
    setClubs((items) => items.filter((club) => club.id !== id));
  };
  const handleAssignLeader = async (club) => {
    const email = (leaderEmails[club.id] ?? club.leaderEmail ?? '').trim();
    if (!email) return;
    setSavingLeader(club.id); setError(''); setMessage('');
    try {
      const updated = await clubManagementService.updateClub(club.id, {
        name: club.name, category: club.category, description: club.description, activities: club.activities,
        eligibility: club.eligibility, department: club.department, leaderEmail: email
      });
      setClubs((items) => items.map((item) => item.id === club.id ? updated : item));
      setLeaderEmails((current) => ({ ...current, [club.id]: updated.leaderEmail || '' }));
      setMessage(`${updated.name} is now assigned to ${updated.leaderEmail}. The student workspace refreshes automatically.`);
    } catch (e) { setError(e.response?.data?.message || 'Club leader could not be assigned.'); }
    finally { setSavingLeader(null); }
  };

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>Club Management</h1>
          <p>Create and govern student clubs across technical, cultural, and sports domains</p>
        </div>

        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <PlusCircle size={18} /> Create New Club
        </button>
      </div>

      {error && <div className="student-card" role="alert">{error}</div>}
      {message && <div className="student-card" role="status">{message}</div>}
      {loading && <div className="student-card" role="status">Loading clubs…</div>}
      {!loading && !clubs.length && !error && <div className="student-card">No clubs have been created.</div>}
      <div className="grid-3">
        {clubs.map(club => (
          <div key={club.id} className="student-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem' }}>
              <span className="badge badge-trust">{club.category}</span>
              <span className="status-badge status-published">{club.status}</span>
            </div>
            <h3 style={{ fontSize: '1.2rem', color: 'var(--primary-dark)', marginBottom: '0.4rem' }}>{club.name}</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--primary-light)', fontWeight: '600', marginBottom: '0.85rem' }}>Leadership: {club.leader}</p>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>Members: {club.membersCount} members</div>

            <div className="form-group"><label className="form-label" htmlFor={`club-leader-${club.id}`}>Student leader email</label><input id={`club-leader-${club.id}`} className="form-input" type="email" value={leaderEmails[club.id] ?? club.leaderEmail ?? ''} onChange={(e) => setLeaderEmails((current) => ({...current,[club.id]:e.target.value}))} placeholder="student@tsdcem.ac.in" /></div>
            <button className="btn btn-outline" style={{width:'100%',marginBottom:'.6rem'}} disabled={savingLeader===club.id} onClick={()=>handleAssignLeader(club)}>{savingLeader===club.id?'Saving…':'Assign / update leader'}</button>

            <button
              className="btn btn-outline"
              style={{ width: '100%', fontSize: '0.82rem', color: club.status === 'Active' ? 'var(--error)' : 'var(--success)' }}
              onClick={() => handleDelete(club.id)}
            >
              Delete club
            </button>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content-box">
            <h3>Create New Student Club</h3>
            <form onSubmit={handleCreate} style={{ marginTop: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Club Name</label>
                <input type="text" className="form-input" placeholder="e.g. Artificial Intelligence Club" value={name} onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Category</label>
                <select className="form-input" value={category} onChange={(e) => setCategory(e.target.value)}>
                  <option value="Technical">Technical</option>
                  <option value="Cultural">Cultural</option>
                  <option value="Sports">Sports</option>
                  <option value="Social">Social</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Designated student leader email</label>
                <input type="email" className="form-input" placeholder="student@tsdcem.ac.in" value={leader} onChange={(e) => setLeader(e.target.value)} required />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Create Club</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
