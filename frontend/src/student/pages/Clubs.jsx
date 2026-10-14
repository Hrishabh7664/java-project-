import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, ClipboardList, ShieldCheck } from 'lucide-react';
import { clubService } from '../services/clubService';
import { ClubCard } from '../components/ClubCard';
import { apiErrorMessage } from '../../services/api';

export const Clubs = () => {
  const [clubs,setClubs]=useState([]);const [error,setError]=useState('');const [loading,setLoading]=useState(true);
  const [query,setQuery]=useState('');const [category,setCategory]=useState('All');
  useEffect(()=>{clubService.getClubs().then(setClubs).catch((e)=>setError(apiErrorMessage(e,'Club directory could not be loaded.'))).finally(()=>setLoading(false));},[]);
  const categories=useMemo(()=>['All',...new Set(clubs.map((c)=>c.category).filter(Boolean))],[clubs]);
  const visible=clubs.filter((c)=>(category==='All'||c.category===category)&&`${c.name} ${c.description||''} ${c.activities||''}`.toLowerCase().includes(query.toLowerCase().trim()));
  return <div><div className="student-page-header"><div className="student-page-title"><h1>Student clubs</h1><p>Explore student groups, learn what they do, and apply to join.</p></div><div style={{display:'flex',gap:'.65rem',flexWrap:'wrap'}}><Link className="btn btn-outline" to="/student/clubs/applications"><ClipboardList size={17}/> My applications</Link><Link className="btn btn-outline" to="/student/clubs/manage"><ShieldCheck size={17}/> Club leadership</Link></div></div>
    <section className="student-card club-filters"><label className="club-search"><Search size={17}/><input aria-label="Search clubs" value={query} onChange={(e)=>setQuery(e.target.value)} placeholder="Search clubs, activities, or interests" /></label><label className="club-category-filter">Category<select className="form-input" value={category} onChange={(e)=>setCategory(e.target.value)}>{categories.map((c)=><option key={c}>{c}</option>)}</select></label></section>
    {loading&&<div className="student-card" role="status">Loading clubs…</div>}{error&&<div className="student-card" role="alert">{error}</div>}
    {!loading&&!error&&visible.length===0&&<div className="student-card">{clubs.length?'No clubs match those filters.':'No clubs are available yet.'}</div>}
    {!error&&visible.length>0&&<div className="grid-3">{visible.map((club)=><ClubCard key={club.id} club={club}/>)}</div>}
  </div>;
};
