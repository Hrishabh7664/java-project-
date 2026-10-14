import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, FileCheck, Send } from 'lucide-react';
import { assignmentService } from '../services/assignmentService';
import { apiErrorMessage } from '../../services/api';

export const AssignmentDetailsFaculty = () => {
  const { id } = useParams(); const navigate = useNavigate();
  const [assignment,setAssignment]=useState(null);const [loading,setLoading]=useState(true);const [error,setError]=useState('');const [message,setMessage]=useState('');const [saving,setSaving]=useState(false);
  const load=async()=>{setLoading(true);setError('');try{setAssignment(await assignmentService.getAssignmentById(id));}catch(e){setError(apiErrorMessage(e,'Assignment could not be loaded.'));}finally{setLoading(false);}};
  useEffect(()=>{load();},[id]);
  const publish=async()=>{setSaving(true);setError('');setMessage('');try{const changed=await assignmentService.updateAssignment(id,{title:assignment.title,description:assignment.description,subjectId:assignment.subjectId,classId:assignment.classId,deadlineAt:assignment.deadlineAt,maximumMarks:assignment.maximumMarks,status:'PUBLISHED',allowResubmission:assignment.allowResubmission,allowLateSubmission:assignment.allowLateSubmission});setAssignment(changed);setMessage('Assignment published for the selected class.');}catch(e){setError(apiErrorMessage(e,'Assignment could not be published.'));}finally{setSaving(false);}};
  if(loading)return <div className="student-card" role="status">Loading assignment details…</div>;
  if(error&&!assignment)return <div><div className="student-card" role="alert">{error}</div><button className="btn btn-outline" onClick={()=>navigate('/faculty/assignments')}>Back to assignments</button></div>;
  return <div><button className="btn btn-outline" style={{marginBottom:'1.25rem'}} onClick={()=>navigate('/faculty/assignments')}><ArrowLeft size={16}/> Back to Assignments</button>
    {error&&<div className="student-card" role="alert">{error}</div>}{message&&<div className="student-card" role="status">{message}</div>}
    <section className="student-card" style={{marginBottom:'1rem'}}><div style={{display:'flex',justifyContent:'space-between',gap:'.5rem',flexWrap:'wrap'}}><span className="badge badge-trust">{assignment.targetClass}</span><span className="status-badge">{assignment.status}</span></div>
      <h1 style={{fontSize:'1.6rem',color:'var(--primary-dark)',margin:'.8rem 0 .4rem'}}>{assignment.title}</h1><p>{assignment.subject} · Maximum {assignment.totalMarks} marks · Due {new Date(assignment.deadlineAt).toLocaleString()}</p><p>Published {assignment.publishedAt?new Date(assignment.publishedAt).toLocaleString():'Not published'} · Resubmission {assignment.allowResubmission?'allowed after request':'disabled'} · Late work {assignment.allowLateSubmission?'accepted':'not accepted'}</p>
      <h2 style={{marginTop:'1rem'}}>Instructions</h2><p style={{whiteSpace:'pre-wrap'}}>{assignment.instructions}</p>
      {assignment.status==='DRAFT'&&<button className="btn btn-primary" disabled={saving} onClick={publish}><Send size={16}/>{saving?'Publishing…':'Publish to class'}</button>}
      <div style={{marginTop:'1rem'}}><button className="btn btn-primary" onClick={()=>navigate(`/faculty/submissions?assignmentId=${assignment.id}`)}><FileCheck size={17}/> Review submissions ({assignment.submissionsCount})</button></div>
    </section>
  </div>;
};
