import api from '../../services/api';
const map = (a) => ({ ...a, subject: a.subjectCode ? `${a.subjectCode} · ${a.subjectName}` : a.subject,
  deadline: a.deadlineAt || a.dueDate, instructions: a.description, totalMarks: a.maximumMarks,
  targetClass: [a.departmentCode, a.academicYear, a.division && `Div ${a.division}`, a.batch && `Batch ${a.batch}`].filter(Boolean).join(' ') });
export const assignmentService = {
  async getAssignments() { return (await api.get('/assignments')).data.map(map); },
  async getAssignmentById(id) { return map((await api.get(`/assignments/${id}`)).data); }
};
