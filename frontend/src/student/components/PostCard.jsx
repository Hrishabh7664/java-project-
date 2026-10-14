import React, { useEffect, useState } from 'react';
import { MessageSquare, Pencil, Trash2, X, Check, Flag } from 'lucide-react';
import { VoteButton } from './VoteButton';
import { CommentSection } from './CommentSection';
import { postService } from '../services/postService';
import { apiErrorMessage } from '../../services/api';
import { useAuth } from '../../auth/hooks/useAuth';

export const PostCard = ({ post, onDelete }) => {
  const { user } = useAuth();
  const [currentPost, setCurrentPost] = useState(post);
  const [showComments, setShowComments] = useState(false);
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(post.title);
  const [content, setContent] = useState(post.content);
  const [error, setError] = useState('');
  const [voting, setVoting] = useState(false);
  const [reporting, setReporting] = useState(false);
  const [reportReason, setReportReason] = useState('');
  const [reportMessage, setReportMessage] = useState('');
  const [reportingCommentId, setReportingCommentId] = useState(null);
  const [commentReportReason, setCommentReportReason] = useState('');

  useEffect(() => {
    setCurrentPost(post);
    setTitle(post.title);
    setContent(post.content);
  }, [post]);

  const handleVote = async (dir) => {
    if (voting) return;
    setVoting(true);
    try {
      const updated = await postService.votePost(currentPost.id, dir);
      setCurrentPost((old) => ({ ...old, userVoted: updated.userVote, votes: updated.score, upvotes: updated.upvotes, downvotes: updated.downvotes }));
      setError('');
    } catch (requestError) {
      setError(apiErrorMessage(requestError));
    } finally { setVoting(false); }
  };

  const handleReport = async (event) => {
    event.preventDefault();
    try { await postService.reportContent('POST', currentPost.id, reportReason.trim());setReportMessage('Your report was submitted for administrator review.');setReportReason('');setReporting(false); }
    catch(requestError){setError(apiErrorMessage(requestError,'Your report could not be submitted.'));}
  };
  const handleCommentReport = async (event) => {
    event.preventDefault();
    try { await postService.reportContent('COMMENT',reportingCommentId,commentReportReason.trim());setReportMessage('Your report was submitted for administrator review.');setReportingCommentId(null);setCommentReportReason(''); }
    catch(requestError){setError(apiErrorMessage(requestError,'Your report could not be submitted.'));}
  };

  const handleAddComment = async (commentText) => {
    try {
      const comment = await postService.addComment(currentPost.id, commentText);
      setCurrentPost((postState) => ({
        ...postState,
        comments: [...(postState.comments || []), comment],
        commentsCount: (postState.commentsCount || 0) + 1
      }));
      setError('');
    } catch (requestError) {
      setError(apiErrorMessage(requestError));
      throw requestError;
    }
  };

  const handleUpdate = async (event) => {
    event.preventDefault();
    try {
      await postService.updatePost(currentPost.id, { title, content, category: currentPost.category });
      setCurrentPost(await postService.getPostById(currentPost.id));
      setEditing(false);
      setError('');
    } catch (requestError) { setError(apiErrorMessage(requestError)); }
  };

  const handleDelete = async () => {
    try {
      await postService.deletePost(currentPost.id);
      onDelete?.(currentPost.id);
    } catch (requestError) { setError(apiErrorMessage(requestError)); }
  };
  const canManage = user?.role === 'ADMIN' || currentPost.authorEmail?.toLowerCase() === user?.email?.toLowerCase();

  return (
    <article className="student-card student-card-hover post-card" style={{ marginBottom: '1.25rem', overflow: 'hidden', minWidth: 0, wordBreak: 'break-word' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.85rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div aria-hidden="true" className="post-avatar">
            {(currentPost.authorName || 'C').slice(0, 1).toUpperCase()}
          </div>
          <div>
            <div style={{ fontWeight: '600', fontSize: '0.92rem' }}>{currentPost.authorName}</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{currentPost.authorRole} • {currentPost.timeAgo}</div>
          </div>
        </div>
        <span className="badge badge-trust" style={{ background: '#f1f5f9', color: 'var(--primary-dark)' }}>
          {currentPost.category}
        </span>
      </div>

      {error && <p role="alert" className="validation-error">{error}</p>}

      {editing ? <form onSubmit={handleUpdate}>
        <input className="form-input" value={title} onChange={(event) => setTitle(event.target.value)} required />
        <textarea className="form-input" rows={4} value={content} onChange={(event) => setContent(event.target.value)} required />
        <button className="btn btn-primary" type="submit"><Check size={15} /> Save</button>
        <button className="btn btn-outline" type="button" onClick={() => setEditing(false)}><X size={15} /> Cancel</button>
      </form> : <>
        <h3 style={{ fontSize: '1.15rem', marginBottom: '0.5rem', color: 'var(--primary-dark)' }}>{currentPost.title}</h3>
        <p style={{ fontSize: '0.95rem', color: 'var(--text-secondary)', marginBottom: '1rem', lineHeight: '1.5' }}>{currentPost.content}</p>
      </>}

      <div className="post-actions">
        <VoteButton votes={currentPost.votes} userVoted={currentPost.userVoted} onVote={handleVote} disabled={voting} />

        <div style={{ display: 'flex', gap: '1rem' }}>
          {canManage && <>
            <button onClick={() => { setEditing(true); setTitle(currentPost.title); setContent(currentPost.content); }} aria-label="Edit post"><Pencil size={16} /></button>
            <button onClick={handleDelete} aria-label="Delete post"><Trash2 size={16} /></button>
          </>}
          <button
            onClick={() => setShowComments(!showComments)}
            style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.88rem', color: 'var(--text-secondary)' }}
          >
            <MessageSquare size={16} /> {currentPost.commentsCount} Comments
          </button>
          {user?.role!=='ADMIN' && currentPost.authorEmail?.toLowerCase()!==user?.email?.toLowerCase() && <button onClick={()=>{setReporting(v=>!v);setReportMessage('');}} aria-label="Report post"><Flag size={16} /> Report</button>}
        </div>
      </div>

      {reportMessage && <p role="status" className="success-notice">{reportMessage}</p>}
      {reporting && <form onSubmit={handleReport} className="report-form">
        <label className="form-label" htmlFor={`report-post-${currentPost.id}`}>Why are you reporting this post?</label>
        <textarea id={`report-post-${currentPost.id}`} className="form-input" value={reportReason} onChange={e=>setReportReason(e.target.value)} required maxLength={1000} rows={2}/>
        <button className="btn btn-primary" type="submit" disabled={!reportReason.trim()}>Submit report</button>
      </form>}

      {showComments && (
        <CommentSection comments={currentPost.comments} onAddComment={handleAddComment} onReport={(id)=>{setReportingCommentId(id);setCommentReportReason('');setReportMessage('');}} />
      )}
      {reportingCommentId && <form onSubmit={handleCommentReport} className="report-form"><label className="form-label" htmlFor={`report-comment-${reportingCommentId}`}>Why are you reporting this comment?</label><textarea id={`report-comment-${reportingCommentId}`} className="form-input" value={commentReportReason} onChange={e=>setCommentReportReason(e.target.value)} required maxLength={1000}/><button className="btn btn-primary" type="submit" disabled={!commentReportReason.trim()}>Submit report</button><button className="btn btn-outline" type="button" onClick={()=>setReportingCommentId(null)}>Cancel</button></form>}
    </article>
  );
};
