import api from '../../services/api';
const map = (a) => ({ ...a, subject: a.subjectCode ? `${a.subjectCode} · ${a.subjectName}` : a.subject,
  deadline: a.deadlineAt || a.dueDate, instructions: a.description, totalMarks: a.maximumMarks,
  targetClass: [a.departmentCode, a.academicYear, a.division && `Div ${a.division}`, a.batch && `Batch ${a.batch}`].filter(Boolean).join(' '), submissionsCount: a.submissionCount ?? 0 });
export const assignmentService = {
  async getTeachingOptions() { return (await api.get('/faculty/attendance/options')).data; },
  async getAssignments() { return (await api.get('/assignments')).data.map(map); },
  async getAssignmentById(id) { return map((await api.get(`/assignments/${id}`)).data); },
  async createAssignment(data) { return map((await api.post('/assignments', data)).data); },
  async updateAssignment(id,data) { return map((await api.put(`/assignments/${id}`,data)).data); },
  async deleteAssignment(id) { await api.delete(`/assignments/${id}`); }
};
