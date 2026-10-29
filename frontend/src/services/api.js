import axios from 'axios';

// Centralized Axios instance configured for Spring Boot REST API integration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
export const TOKEN_KEY = 'campus_connect_token';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

api.interceptors.request.use(
  (config) => {
    const token = window.localStorage.getItem(TOKEN_KEY);
    if (token) config.headers.Authorization = `Bearer ${token}`;
    // Keep Axios and the browser from sending JSON headers with multipart bodies.
    // The browser adapter will add the multipart boundary when it sends FormData.
    if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
      config.headers.delete('Content-Type');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && window.localStorage.getItem(TOKEN_KEY)) {
      window.localStorage.removeItem(TOKEN_KEY);
      window.localStorage.removeItem('campus_connect_user');
      window.dispatchEvent(new Event('campus-connect:unauthorized'));
    }
    return Promise.reject(error);
  }
);

export function apiErrorMessage(error, fallback = 'The request could not be completed. Please try again.') {
  const status = error.response?.status;
  const serverMessage = error.response?.data?.message || error.response?.data?.detail;
  if (!error.response) return 'Unable to reach Campus Connect. Check that the API is running and try again.';
  if (status === 400 || status === 422) return serverMessage || 'Some information is invalid. Check the form and try again.';
  if (status === 401) return serverMessage || 'Your session has expired. Please sign in again.';
  if (status === 403) return serverMessage || 'You do not have permission to do that.';
  if (status === 404) return 'That item could not be found.';
  if (status === 409) return serverMessage || 'An account with that email already exists.';
  return serverMessage || fallback;
}

export default api;
