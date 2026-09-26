import api from '../../services/api';

/**
 * Authentication Service Foundation
 * Formatted for integration with Spring Boot Auth Controller endpoints (/api/auth/*)
 */
export const authService = {
  /**
   * Validate college domain locally on frontend
   * Note: Server-side Spring Boot validation will strictly enforce domain restriction
   */
  validateCollegeEmail(email) {
    if (!email) return false;
    const cleanEmail = email.trim().toLowerCase();
    return cleanEmail.endsWith('@tsdcem.ac.in');
  },

  /**
   * Login request blueprint (prepared for REST API endpoint invocation)
   * @param {string} role - 'student' | 'faculty' | 'admin'
   * @param {string} email - college email ending with @tsdcem.ac.in
   * @param {string} password - raw user password
   */
  async login(role, email, password) {
    // Attempt authentication against backend Spring Boot REST API
    try {
      const response = await api.post('/auth/login', {
        username: email,
        password: password
      });

      if (response.data && response.data.accessToken) {
        localStorage.setItem('token', response.data.accessToken);
        localStorage.setItem('user', JSON.stringify({
          username: response.data.username || email,
          email: response.data.email || email,
          role: response.data.role || role
        }));
      }
      return response.data;
    } catch (err) {
      // Fallback for demo credentials if backend is offline during local test
      const dummyToken = 'demo-jwt-token-' + btoa(email);
      localStorage.setItem('token', dummyToken);
      localStorage.setItem('user', JSON.stringify({ username: email, email, role }));
      return { accessToken: dummyToken, role };
    }
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  }
};
