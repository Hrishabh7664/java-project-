import React from 'react';
import { ArrowBigUp, ArrowBigDown } from 'lucide-react';

export const VoteButton = ({ votes, userVoted, onVote, disabled }) => {
  return (
    <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem', background: '#f1f5f9', borderRadius: 'var(--radius-pill)', padding: '0.2rem 0.6rem' }}>
      <button
        onClick={() => onVote(1)}
        disabled={disabled}
        aria-pressed={userVoted === 1}
        style={{ color: userVoted === 1 ? 'var(--primary-light)' : 'var(--text-muted)' }}
        aria-label="Upvote"
      >
        <ArrowBigUp size={20} fill={userVoted === 1 ? 'currentColor' : 'none'} />
      </button>

      <span style={{ fontWeight: '700', fontSize: '0.85rem', minWidth: '20px', textAlign: 'center' }}>
        {votes}
      </span>

      <button
        onClick={() => onVote(-1)}
        disabled={disabled}
        aria-pressed={userVoted === -1}
        style={{ color: userVoted === -1 ? 'var(--error)' : 'var(--text-muted)' }}
        aria-label="Downvote"
      >
        <ArrowBigDown size={20} fill={userVoted === -1 ? 'currentColor' : 'none'} />
      </button>
    </div>
  );
};
