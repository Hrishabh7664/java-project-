import api from '../../services/api';
export const timetableService = {
  async getTimetable() { return (await api.get('/faculty/timetable')).data; },
  async updateSyllabus(subjectId, syllabus) {
    return (await api.put(`/faculty/subjects/${subjectId}/syllabus`, { syllabus })).data;
  },
  async updateSchedule(assignmentId, schedule) { return (await api.put(`/faculty/timetable/${assignmentId}`, schedule)).data; }
};
