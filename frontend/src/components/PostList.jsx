import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import PostForm from './PostForm';
import { fetchPosts, createPost, votePost } from '../services/apiService';
import { useAuth } from '../context/AuthContext';

const PostList = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [filterCategory, setFilterCategory] = useState('All');
  const [alert, setAlert] = useState(null);

  const loadPosts = async () => {
    try {
      setLoading(true);
      const data = await fetchPosts();
      setPosts(data);
    } catch (err) {
      console.error('Error fetching posts:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPosts();
  }, []);

  const handlePostCreated = async (postData, onSuccess) => {
    setIsSubmitting(true);
    try {
      const payload = {
        ...postData,
        authorName: user?.username || 'Student User'
      };
      const newPost = await createPost(payload);
      setPosts((prev) => [newPost, ...prev]);
      setAlert({ message: 'Post published to campus feed!', type: 'success' });
      if (onSuccess) onSuccess();
      setTimeout(() => setAlert(null), 4000);
    } catch (err) {
      console.error('Failed to create post:', err);
      setAlert({ message: 'Failed to create post. Please try again.', type: 'danger' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleVote = async (postId, delta) => {
    try {
      const updated = await votePost(postId, delta);
      setPosts((prev) =>
        prev.map((p) => (p.id === postId ? { ...p, voteCount: updated.voteCount } : p))
      );
    } catch (err) {
      console.error('Vote failed:', err);
    }
  };

  const filteredPosts = filterCategory === 'All'
    ? posts
    : posts.filter((p) => p.category === filterCategory);

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <Navbar />

      <main className="flex-grow-1 py-4 container">
        <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <div>
            <h3 className="fw-bold mb-1 text-dark">
              <i className="bi bi-chat-square-text text-primary me-2"></i> Campus Social Hub
            </h3>
            <p className="text-muted small mb-0">Reddit-style campus discussions, category boards, and peer insights.</p>
          </div>

          <div className="btn-group" role="group">
            {['All', 'General', 'Technology', 'Academics'].map((cat) => (
              <button
                key={cat}
                type="button"
                className={`btn btn-sm ${filterCategory === cat ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => setFilterCategory(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        <PostForm onPostCreated={handlePostCreated} isSubmitting={isSubmitting} />

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading posts...</span>
            </div>
          </div>
        ) : filteredPosts.length > 0 ? (
          <div className="d-flex flex-column gap-3">
            {filteredPosts.map((post) => (
              <div key={post.id} className="card shadow-sm border-0 p-4" style={{ borderRadius: '12px' }}>
                <div className="d-flex align-items-start gap-3">
                  {/* Upvote Column */}
                  <div className="d-flex flex-column align-items-center bg-light p-2 rounded-3" style={{ minWidth: '48px' }}>
                    <button
                      className="btn btn-link p-0 text-muted"
                      onClick={() => handleVote(post.id, 1)}
                      title="Upvote"
                    >
                      <i className="bi bi-arrow-up-circle-fill fs-5 text-primary"></i>
                    </button>
                    <span className="fw-bold my-1 text-dark">{post.voteCount || 0}</span>
                    <button
                      className="btn btn-link p-0 text-muted"
                      onClick={() => handleVote(post.id, -1)}
                      title="Downvote"
                    >
                      <i className="bi bi-arrow-down-circle fs-5"></i>
                    </button>
                  </div>

                  {/* Post Content */}
                  <div className="flex-grow-1">
                    <div className="d-flex justify-content-between align-items-center mb-1">
                      <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                        {post.category || 'General'}
                      </span>
                      <small className="text-muted">
                        <i className="bi bi-clock me-1"></i>
                        {post.createdAt ? new Date(post.createdAt).toLocaleDateString() : 'Recent'}
                      </small>
                    </div>

                    <h5 className="fw-bold text-dark mb-2">{post.title}</h5>
                    <p className="text-secondary mb-3">{post.content}</p>

                    <div className="d-flex justify-content-between align-items-center border-top pt-2">
                      <small className="text-muted">
                        <i className="bi bi-person-circle me-1"></i> Posted by <strong>{post.authorName || 'Campus Member'}</strong>
                      </small>
                      <span className="small text-muted">
                        <i className="bi bi-chat-left-dots me-1"></i> Discussion Active
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="card shadow-sm border-0 p-5 text-center text-muted" style={{ borderRadius: '12px' }}>
            <i className="bi bi-chat-dots fs-1 mb-2"></i>
            <p className="mb-0">No posts found in this category. Be the first to start a conversation!</p>
          </div>
        )}
      </main>

      <footer className="py-3 bg-white border-top text-center text-muted small mt-auto">
        &copy; 2026 Campus Connect &mdash; Production Full Stack Academic Platform
      </footer>
    </div>
  );
};

export default PostList;
