import api from '../../services/api';

export const attendanceService = {
  async getAttendance() {
    const response = await api.get('/student/attendance');
    return response.data;
  }
};
