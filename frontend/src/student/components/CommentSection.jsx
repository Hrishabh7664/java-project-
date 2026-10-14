import React, { useState } from 'react';
import { Comment } from './Comment';
import { Send } from 'lucide-react';

export const CommentSection = ({ comments, onAddComment, onReport }) => {
  const [text, setText] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const value = text.trim();
    if (!value || submitting) return;
    setSubmitting(true);
    try {
      await onAddComment(value);
      setText('');
    } catch {
      // The post card exposes the API error; keep the unsent text available to retry.
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border-light)' }}>
      <h4 style={{ fontSize: '0.95rem', marginBottom: '1rem' }}>Comments ({comments.length})</h4>

      <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
        <input
          type="text"
          className="form-input"
          placeholder="Write a comment..."
          value={text}
          onChange={(e) => setText(e.target.value)}
          maxLength={2000}
          aria-label="Write a comment"
          style={{ padding: '0.5rem 1rem' }}
        />
        <button type="submit" className="btn btn-primary" style={{ padding: '0.5rem 1.25rem' }} disabled={submitting || !text.trim()} aria-label="Post comment">
          <Send size={16} /> {submitting ? 'Sending' : ''}
        </button>
      </form>

      <div>
        {comments.map(c => <Comment key={c.id} comment={c} onReport={onReport} />)}
      </div>
    </div>
  );
};
