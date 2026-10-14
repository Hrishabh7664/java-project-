import api from '../../services/api';
export const timetableService = {
  async getTimetable() { return (await api.get('/student/timetable')).data; }
};
