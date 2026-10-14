import React,{useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import {ClipboardList,RotateCcw} from 'lucide-react';
import {clubService} from '../services/clubService';
import {apiErrorMessage} from '../../services/api';
export const ClubApplications=()=>{
 const [rows,setRows]=useState([]);const [loading,setLoading]=useState(true);const [error,setError]=useState('');const [message,setMessage]=useState('');const [busy,setBusy]=useState(null);
 const load=async()=>{setLoading(true);setError('');try{setRows(await clubService.getMyApplications());}catch(e){setError(apiErrorMessage(e,'Your application history could not be loaded.'));}finally{setLoading(false);}};
 useEffect(()=>{load();},[]);
 const withdraw=async(id)=>{setBusy(id);setError('');setMessage('');try{await clubService.withdraw(id);setMessage('Pending application withdrawn.');await load();}catch(e){setError(apiErrorMessage(e,'The application could not be withdrawn.'));}finally{setBusy(null);}};
 return <div><div className="student-page-header"><div className="student-page-title"><h1>My club applications</h1><p>Track decisions and manage requests you have submitted.</p></div><Link to="/student/clubs" className="btn btn-outline">Browse clubs</Link></div>
 {error&&<div role="alert" className="student-card">{error}</div>}{message&&<div role="status" className="student-card">{message}</div>}
 {loading?<div className="student-card" role="status">Loading your application history…</div>:!rows.length?<div className="student-card"><ClipboardList size={20}/> You have not applied to any clubs yet.</div>:<div className="club-application-list">{rows.map((a)=><article className="student-card" key={a.id}><div className="club-application-heading"><div><h2><Link to={`/student/clubs/${a.clubId}`}>{a.clubName}</Link></h2><p>Submitted {new Date(a.submittedAt).toLocaleString()}</p></div><span className={`club-application-status status-${a.status.toLowerCase()}`}>{a.status.replaceAll('_',' ')}</span></div>
 {a.motivation&&<p className="club-motivation">{a.motivation}</p>}{a.decidedAt&&<div className="club-decision-record"><strong>Decision by {a.decidedByName||'club leader'}</strong><span>{new Date(a.decidedAt).toLocaleString()}</span>{a.decisionNote&&<p>{a.decisionNote}</p>}</div>}
 {a.status==='PENDING'&&<button className="btn btn-outline" disabled={busy===a.id} onClick={()=>withdraw(a.id)}><RotateCcw size={15}/>{busy===a.id?'Withdrawing…':'Withdraw application'}</button>}
 </article>)}</div>}
 </div>;
};
