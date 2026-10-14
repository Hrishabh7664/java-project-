import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { assignmentService } from '../services/assignmentService';
import { AssignmentCardFaculty } from '../components/AssignmentCard';
import { PlusCircle } from 'lucide-react';
import { apiErrorMessage } from '../../services/api';

export const Assignments = () => {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

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
          <h1>Course Assignments</h1>
          <p>Create, manage, and grade student assignments</p>
        </div>

        <button className="btn btn-primary" onClick={() => navigate('/faculty/assignments/create')}>
          <PlusCircle size={18} /> Create New Assignment
        </button>
      </div>

      {error && <div className="student-card" role="alert">{error}</div>}
      {loading ? <div className="student-card" role="status">Loading assignments…</div> : !error && !assignments.length ? <div className="student-card">No assignments have been created yet.</div> : <div className="grid-2">
        {assignments.map(a => <AssignmentCardFaculty key={a.id} assignment={a} />)}
      </div>}
    </div>
  );
};
