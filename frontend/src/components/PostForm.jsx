import React, { useState } from 'react';

const PostForm = ({ onPostCreated, isSubmitting }) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('General');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault(); // Controlled submit handling
    setError('');

    if (!title.trim() || !content.trim()) {
      setError('Please provide both a post title and content.');
      return;
    }

    onPostCreated({ title: title.trim(), content: content.trim(), category }, () => {
      setTitle('');
      setContent('');
      setCategory('General');
    });
  };

  return (
    <div className="card shadow-sm border-0 p-4 mb-4" style={{ borderRadius: '12px' }}>
      <h5 className="fw-bold mb-3 text-dark">
        <i className="bi bi-pencil-square text-primary me-2"></i> Create a Discussion Post
      </h5>

      {error && (
        <div className="alert alert-danger py-2 small mb-3">
          <i className="bi bi-exclamation-triangle-fill me-1"></i> {error}
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate>
        <div className="row g-3 mb-3">
          <div className="col-12 col-md-8">
            <label className="form-label small fw-semibold">Post Title</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Tips for upcoming Placement Season 2026"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              disabled={isSubmitting}
              required
            />
          </div>
          <div className="col-12 col-md-4">
            <label className="form-label small fw-semibold">Category Board</label>
            <select
              className="form-select"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              disabled={isSubmitting}
            >
              <option value="General">General Campus</option>
              <option value="Technology">Technology &amp; Coding</option>
              <option value="Academics">Academics &amp; Exams</option>
              <option value="Placements">Placements &amp; Careers</option>
              <option value="Events">Clubs &amp; Cultural</option>
            </select>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">Discussion Content</label>
          <textarea
            className="form-control"
            rows="3"
            placeholder="Share your thoughts, resources, or questions with fellow students..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            disabled={isSubmitting}
            required
          ></textarea>
        </div>

        <div className="d-flex justify-content-end">
          <button type="submit" className="btn btn-primary rounded-pill px-4" disabled={isSubmitting}>
            {isSubmitting ? 'Publishing...' : 'Publish Post'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PostForm;
