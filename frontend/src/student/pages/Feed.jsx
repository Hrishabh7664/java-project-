import React, { useState, useEffect, useCallback, useRef } from 'react';
import { postService } from '../services/postService';
import { PostCard } from '../components/PostCard';
import { CreatePostModal } from '../components/CreatePost';
import { PlusCircle, RefreshCw, Search } from 'lucide-react';
import { apiErrorMessage } from '../../services/api';

export const Feed = () => {
  const [posts, setPosts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCat, setSelectedCat] = useState('All');
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [query, setQuery] = useState('');
  const [sort, setSort] = useState('recent');
  const requestSequence = useRef(0);

  const refresh = useCallback(async () => {
    const sequence = ++requestSequence.current;
    setLoading(true);
    setError('');
    try {
      const p = await postService.getPosts(selectedCat, {search: query, sort});
      if (sequence === requestSequence.current) setPosts(p);
    } catch (requestError) {
      if (sequence === requestSequence.current) setError(apiErrorMessage(requestError, 'Posts could not be loaded.'));
    } finally {
      if (sequence === requestSequence.current) setLoading(false);
    }
  }, [selectedCat, query, sort]);

  useEffect(() => {
    postService.getCategories().then((items) => setCategories(['All', ...items])).catch((requestError) => {
      setError(apiErrorMessage(requestError, 'Post categories could not be loaded.'));
    });
  }, []);

  useEffect(() => {
    refresh();
    return () => { requestSequence.current += 1; };
  }, [refresh]);

  const handleCreatePost = async (postData) => {
    await postService.createPost(postData);
    await refresh();
  };
  const visiblePosts = posts;

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>Campus Social Feed</h1>
          <p>Campus-wide discussions, posts, voting, and student interactions</p>
        </div>

        <button className="btn btn-primary" onClick={() => setIsCreateOpen(true)}>
          <PlusCircle size={18} /> Create Post
        </button>
      </div>

      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1.5rem' }}>
        {categories.map(cat => (
          <button
            key={cat}
            className={`btn ${selectedCat === cat ? 'btn-primary' : 'btn-outline'}`}
            style={{ padding: '0.35rem 0.85rem', fontSize: '0.82rem' }}
            onClick={() => setSelectedCat(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      <div className="feed-tools">
        <label className="feed-search"><Search size={17}/><input aria-label="Search discussions" placeholder="Search posts, topics, or categories" value={query} onChange={e=>setQuery(e.target.value)}/></label>
        <label className="feed-sort-label">Sort <select className="form-input" value={sort} onChange={e=>setSort(e.target.value)}><option value="recent">Most recent</option><option value="popular">Most popular</option></select></label>
      </div>

      <div>
        {loading && <p role="status" className="student-card">Loading campus posts…</p>}
        {error && <div className="student-card" role="alert" style={{ color: 'var(--error)' }}>{error} <button className="btn btn-outline" onClick={refresh}><RefreshCw size={15} /> Retry</button></div>}
        {!loading && !error && posts.length === 0 && <div className="student-card">No posts in this category yet. Start a campus conversation.</div>}
        {visiblePosts.map(post => (
          <PostCard key={post.id} post={post} onDelete={(id) => setPosts((items) => items.filter((item) => item.id !== id))} />
        ))}
      </div>

      <CreatePostModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onCreate={handleCreatePost}
      />
    </div>
  );
};
