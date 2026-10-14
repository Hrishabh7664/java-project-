import React, { useEffect, useState } from 'react';
import { useParams,useNavigate,Link } from 'react-router-dom';
import { ArrowLeft,Users,Award,Send,RotateCcw } from 'lucide-react';
import { clubService } from '../services/clubService';
import { apiErrorMessage } from '../../services/api';

export const ClubDetails = () => {
  const {id}=useParams();const navigate=useNavigate();const [club,setClub]=useState(null);const [history,setHistory]=useState([]);const [motivation,setMotivation]=useState('');
  const [loading,setLoading]=useState(true);const [saving,setSaving]=useState(false);const [error,setError]=useState('');const [message,setMessage]=useState('');
  const load=async()=>{setLoading(true);setError('');try{const [c,apps]=await Promise.all([clubService.getClubById(id),clubService.getMyApplications()]);setClub(c);setHistory(apps.filter((a)=>String(a.clubId)===String(id)));}catch(e){setError(apiErrorMessage(e,'Club details could not be loaded.'));}finally{setLoading(false);}};
  useEffect(()=>{load();},[id]);
  const submit=async(e)=>{e.preventDefault();setSaving(true);setError('');setMessage('');try{await clubService.apply(id,motivation);setMotivation('');setMessage('Your membership application was sent.');await load();}catch(e2){setError(apiErrorMessage(e2,'Your application could not be submitted.'));}finally{setSaving(false);}};
  const withdraw=async(appId)=>{setSaving(true);setError('');setMessage('');try{await clubService.withdraw(appId);setMessage('Your pending application was withdrawn. You may apply again later.');await load();}catch(e){setError(apiErrorMessage(e,'The application could not be withdrawn.'));}finally{setSaving(false);}};
  if(loading)return <div className="student-card" role="status">Loading club information…</div>;
  if(error&&!club)return <div><div className="student-card" role="alert">{error}</div><button className="btn btn-outline" onClick={()=>navigate('/student/clubs')}><ArrowLeft size={16}/> Back to clubs</button></div>;
  const latest=history[0];
  return <div><button className="btn btn-outline" style={{marginBottom:'1.1rem'}} onClick={()=>navigate('/student/clubs')}><ArrowLeft size={16}/> Back to clubs</button>
    {error&&<div className="student-card" role="alert">{error}</div>}{message&&<div className="student-card" role="status">{message}</div>}
    <section className="student-card club-detail-hero"><div><span className="badge badge-trust">{club.category}</span>{club.demoContent&&<span className="badge club-demo-badge">Demo club</span>}<h1>{club.name}</h1><p>{club.description||'Description not provided.'}</p><div className="club-facts"><span><Users size={16}/>{club.membersCount} active members</span><span>{club.department||'All departments'}</span><span>Leader: {club.leaderName||'Not assigned'}</span></div></div></section>
    <div className="grid-2" style={{marginTop:'1rem'}}><section className="student-card"><h2><Award size={18}/> Leadership</h2>{club.leaderName?<p>{club.leaderName} · Club leader</p>:<p className="text-muted">A leader has not been assigned.</p>}
      <h3>Activities</h3><p style={{whiteSpace:'pre-wrap'}}>{club.activities||'Activities have not been listed yet.'}</p><h3>Eligibility</h3><p style={{whiteSpace:'pre-wrap'}}>{club.eligibility||'See the club leader for membership details.'}</p></section>
      <section className="student-card"><h2>Your membership</h2>{club.isMember?<p className="club-status-success">You are an active member of this club.</p>:latest?<p>Latest application: <strong>{latest.status.replaceAll('_',' ')}</strong> · {new Date(latest.submittedAt).toLocaleString()}</p>:<p>You have not applied to this club.</p>}
        {latest?.status==='PENDING'&&<button className="btn btn-outline" onClick={()=>withdraw(latest.id)} disabled={saving}><RotateCcw size={16}/>{saving?'Working…':'Withdraw pending application'}</button>}
        {club.canApply&&<form onSubmit={submit} style={{marginTop:'1rem'}}><div className="form-group"><label className="form-label" htmlFor="club-motivation">Why would you like to join? <span className="form-hint">Optional</span></label><textarea id="club-motivation" className="form-input" rows={4} maxLength={3000} value={motivation} onChange={(e)=>setMotivation(e.target.value)} placeholder="Share your interests or experience"/></div><button className="btn btn-primary" type="submit" disabled={saving}><Send size={16}/>{saving?'Sending…':'Apply to join'}</button></form>}
        {latest?.status==='REJECTED'&&<p className="club-status-muted">This club does not accept reapplications after a rejection.</p>}
        {club.isLeader&&<p style={{marginTop:'1rem'}}><Link className="btn btn-outline" to="/student/clubs/manage">Manage club applications</Link></p>}
      </section></div>
    {history.length>0&&<section className="student-card" style={{marginTop:'1rem'}}><h2>Application history</h2><ul className="club-history">{history.map((a)=><li key={a.id}><strong>{a.status.replaceAll('_',' ')}</strong><span>Submitted {new Date(a.submittedAt).toLocaleString()}</span>{a.decidedAt&&<span>Decision by {a.decidedByName||'Club leader'} · {new Date(a.decidedAt).toLocaleString()}</span>}{a.decisionNote&&<p>{a.decisionNote}</p>}</li>)}</ul></section>}
  </div>;
};
