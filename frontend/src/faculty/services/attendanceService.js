import api from '../../services/api';

export const attendanceService = {
  async getOptions() {
    const response = await api.get('/faculty/attendance/options');
    return response.data;
  },
  async getRoster(assignmentId, date) {
    const response = await api.get('/faculty/attendance/roster', { params: { assignmentId, date } });
    return response.data;
  },
  async getAttendanceHistory() {
    const response = await api.get('/faculty/attendance/sessions');
    return response.data;
  },
  async saveAttendance(data, sessionId) {
    const response = sessionId
      ? await api.put(`/faculty/attendance/sessions/${sessionId}`, data)
      : await api.post('/faculty/attendance/sessions', data);
    return response.data;
  }
};
