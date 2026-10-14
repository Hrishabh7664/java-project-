import React, {useState} from 'react';
import { Flag } from 'lucide-react';
import { Pencil, Trash2, Check, X } from 'lucide-react';
import { postService } from '../services/postService';
import { apiErrorMessage } from '../../services/api';
import { useAuth } from '../../auth/hooks/useAuth';

export const Comment = ({ comment, onReport }) => {
  const {user}=useAuth();const [editing,setEditing]=useState(false);const [text,setText]=useState(comment.content);const [error,setError]=useState('');
  const mine=comment.authorEmail?.toLowerCase()===user?.email?.toLowerCase();
  const save=async()=>{try{await postService.updateComment(comment.id,text);comment.content=text;setEditing(false);setError('');}catch(e){setError(apiErrorMessage(e));}};
  const remove=async()=>{if(!window.confirm('Delete your comment?'))return;try{await postService.deleteComment(comment.id);comment.deleted=true;}catch(e){setError(apiErrorMessage(e));}};
  if(comment.deleted)return null;
  return (
    <div style={{ padding: '0.75rem 0', borderBottom: '1px solid var(--border-light)' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
        <span style={{ fontWeight: '600', fontSize: '0.85rem', color: 'var(--text-primary)' }}>{comment.authorName}</span>
        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{comment.timeAgo}</span>
      </div>
      <div style={{ fontSize: '0.78rem', color: 'var(--primary-light)', marginBottom: '0.35rem' }}>{comment.authorRole}</div>
      {editing?<><textarea className="form-input" value={text} onChange={e=>setText(e.target.value)}/><button className="btn btn-primary" onClick={save}><Check size={14}/> Save</button><button className="btn btn-outline" onClick={()=>setEditing(false)}><X size={14}/> Cancel</button></>:<p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)' }}>{comment.content}</p>}
      {error&&<p role="alert" className="validation-error">{error}</p>}
      {mine&&<><button type="button" className="btn btn-outline" style={{fontSize:'.75rem',padding:'.2rem .5rem'}} onClick={()=>setEditing(true)}><Pencil size={13}/> Edit</button><button type="button" className="btn btn-outline" style={{fontSize:'.75rem',padding:'.2rem .5rem'}} onClick={remove}><Trash2 size={13}/> Delete</button></>}
      {!mine&&<button type="button" className="btn btn-outline" style={{fontSize:'.75rem',padding:'.2rem .5rem'}} onClick={()=>onReport?.(comment.id)}><Flag size={13}/> Report</button>}
    </div>
  );
};
