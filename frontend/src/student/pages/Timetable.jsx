import React, { useEffect, useState } from 'react';
import { CalendarDays, MapPin } from 'lucide-react';
import { timetableService } from '../services/timetableService';
import { apiErrorMessage } from '../../services/api';

const week = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
export const StudentTimetable = () => {
  const [entries,setEntries]=useState([]);const [error,setError]=useState('');const [loading,setLoading]=useState(true);
  useEffect(()=>{timetableService.getTimetable().then(setEntries).catch((e)=>setError(apiErrorMessage(e,'Your timetable could not be loaded.'))).finally(()=>setLoading(false));},[]);
  const days=week.map((day)=>[day,entries.filter((x)=>x.dayOfWeek?.toUpperCase()===day).sort((a,b)=>a.startTime.localeCompare(b.startTime))]);
  return <div><div className="student-page-header"><div className="student-page-title"><h1>My timetable</h1><p>Scheduled lectures for your enrolled classes, with subject syllabus and locations.</p></div></div>
    {error&&<div className="student-card" role="alert">{error}</div>}{loading?<div className="student-card" role="status">Loading your timetable…</div>:!entries.length?<div className="student-card">No scheduled lectures are available for your enrollment.</div>:<div className="timetable-grid">{days.filter(([,items])=>items.length).map(([day,items])=><section className="student-card timetable-day" key={day}><h2><CalendarDays size={18}/>{day.charAt(0)+day.slice(1).toLowerCase()}</h2>{items.map((entry)=><article className="timetable-slot" key={entry.assignmentId}><div className="timetable-slot-meta">{entry.startTime}–{entry.endTime} · Semester {entry.semester}</div><h3>{entry.subjectCode} · {entry.subjectName}</h3><p>{entry.departmentCode} {entry.academicYear} · Division {entry.division} · Batch {entry.batch}</p><p>Faculty: {entry.facultyName}</p><p><MapPin size={15}/> {entry.classroom||'Room not set'}</p><details className="syllabus-editor"><summary>View syllabus</summary><p style={{whiteSpace:'pre-wrap'}}>{entry.syllabus||'Syllabus has not been added yet.'}</p></details></article>)}</section>)}</div>}
  </div>;
};
