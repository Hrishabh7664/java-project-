import React, { useState, useEffect } from 'react';
import { assignmentService } from '../services/assignmentService';
import { AssignmentCard } from '../components/AssignmentCard';
import { apiErrorMessage } from '../../services/api';

export const MyAssignments = () => {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      try { const asgns = await assignmentService.getAssignments(); setAssignments(asgns); }
      catch (e) { setError(apiErrorMessage(e, 'Assignments could not be loaded.')); }
      finally { setLoading(false); }
    }
    load();
  }, []);

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>My Academic Assignments</h1>
          <p>Track course assignments, submission status, and faculty marks</p>
        </div>
      </div>

      {error && <div className="student-card" role="alert">{error}</div>}
      {loading ? <div className="student-card" role="status">Loading assignments…</div> : !error && !assignments.length ? <div className="student-card">No published assignments are available for your enrolled classes.</div> : <div className="grid-2">
        {assignments.map(a => <AssignmentCard key={a.id} assignment={a} />)}
      </div>}
    </div>
  );
};
