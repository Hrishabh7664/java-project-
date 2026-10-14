import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserCheck, CalendarClock } from 'lucide-react';
import { attendanceService } from '../services/attendanceService';
import { apiErrorMessage } from '../../services/api';

export const AttendanceFaculty = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  useEffect(() => {
    attendanceService.getAttendanceHistory().then(setRecords)
      .catch((err) => setError(apiErrorMessage(err, 'Attendance history could not be loaded.')))
      .finally(() => setLoading(false));
  }, []);

  return <div>
    <div className="student-page-header"><div className="student-page-title"><h1>Attendance Portal</h1><p>Take date-wise registers for assigned classes and review saved attendance.</p></div>
      <button className="btn btn-primary" onClick={() => navigate('/faculty/attendance/take')}><UserCheck size={18} /> Take Attendance</button>
    </div>
    {error && <div className="student-card" role="alert">{error}</div>}
    {loading ? <div className="student-card" role="status">Loading saved attendance…</div> : !error && !records.length ? <div className="student-card"><CalendarClock size={20} /> No attendance has been recorded for your assigned classes.</div> : !error &&
      <div className="admin-table-container"><table className="admin-table">
        <thead><tr><th>Date</th><th>Subject</th><th>Class / Lecture</th><th>Present</th><th>Absent</th><th>Students</th><th>Attendance</th></tr></thead>
        <tbody>{records.map((record) => <tr key={record.id}>
          <td>{record.date}</td><td><strong>{record.subjectCode}</strong><br />{record.subjectName}</td>
          <td>{record.departmentCode} {record.academicYear} Div {record.division} Batch {record.batch}<br />Lecture {record.lectureNumber}</td>
          <td>{record.presentCount}</td><td>{record.absentCount}</td><td>{record.totalCount}</td>
          <td style={{ fontWeight: 700, color: record.percentage >= 75 ? 'var(--success)' : 'var(--error)' }}>{record.percentage}%</td>
        </tr>)}</tbody>
      </table></div>}
  </div>;
};
