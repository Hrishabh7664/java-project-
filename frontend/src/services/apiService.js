import axios from 'axios';

// Centralized Axios client configured for Spring Boot REST API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request Interceptor: Attach JWT Bearer Token if present (Experiment 6)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Catch 401 Unauthorized and redirect to login (Experiment 6)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// ---------------- Authentication APIs (Exp 6) ----------------
export const login = async (username, password) => {
  const response = await api.post('/auth/login', { username, password });
  if (response.data && response.data.accessToken) {
    localStorage.setItem('token', response.data.accessToken);
    localStorage.setItem('user', JSON.stringify({
      username: response.data.username,
      email: response.data.email,
      role: response.data.role
    }));
  }
  return response.data;
};

export const register = async (userData) => {
  const response = await api.post('/auth/register', userData);
  return response.data;
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '/login';
};

export const getToken = () => localStorage.getItem('token');
export const getCurrentUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};
export const isAuthenticated = () => !!localStorage.getItem('token');

// ---------------- Social Hub / Posts APIs ----------------
export const fetchPosts = async () => (await api.get('/posts')).data;
export const fetchPostById = async (id) => (await api.get(`/posts/${id}`)).data;
export const createPost = async (postData) => (await api.post('/posts', postData)).data;
export const votePost = async (id, delta = 1) => (await api.post(`/posts/${id}/vote?delta=${delta}`)).data;
export const deletePost = async (id) => (await api.delete(`/posts/${id}`)).data;

// ---------------- Comments APIs ----------------
export const fetchCommentsByPost = async (postId) => (await api.get(`/comments/post/${postId}`)).data;
export const createComment = async (commentData) => (await api.post('/comments', commentData)).data;

// ---------------- Clubs APIs ----------------
export const fetchClubs = async () => (await api.get('/clubs')).data;
export const createClub = async (clubData) => (await api.post('/clubs', clubData)).data;

// ---------------- Events APIs ----------------
export const fetchEvents = async () => (await api.get('/events')).data;
export const createEvent = async (eventData) => (await api.post('/events', eventData)).data;

// ---------------- Notices APIs ----------------
export const fetchNotices = async () => (await api.get('/notices')).data;
export const createNotice = async (noticeData) => (await api.post('/notices', noticeData)).data;

// ---------------- Assignments APIs ----------------
export const fetchAssignments = async () => (await api.get('/assignments')).data;
export const createAssignment = async (data) => (await api.post('/assignments', data)).data;

// ---------------- Elections APIs ----------------
export const fetchElections = async () => (await api.get('/elections')).data;
export const createElection = async (data) => (await api.post('/elections', data)).data;

// ---------------- Complaints APIs ----------------
export const fetchComplaints = async () => (await api.get('/complaints')).data;
export const createComplaint = async (data) => (await api.post('/complaints', data)).data;

// ---------------- Students APIs ----------------
export const fetchStudents = async () => (await api.get('/students')).data;
export const createStudent = async (data) => (await api.post('/students', data)).data;

// ---------------- Banking APIs (Exp 2 & 3) ----------------
export const fetchStats = async () => (await api.get('/dashboard/stats')).data;
export const fetchTransactions = async () => (await api.get('/transactions')).data;
export const fetchAccounts = async () => (await api.get('/accounts')).data;
export const postNewAccount = async (accountData) => (await api.post('/accounts', accountData)).data;
export const postTransfer = async (transferData) => (await api.post('/transactions', transferData)).data;

export default api;
