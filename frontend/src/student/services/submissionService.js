import api from '../../services/api';
export const submissionService = {
  async getMine(assignmentId) { return (await api.get(`/student/assignments/${assignmentId}/submission`)).data; },
  async submit(assignmentId, file) {
    const body = new FormData(); body.append('file', file);
    return (await api.post(`/student/assignments/${assignmentId}/submission`, body, { timeout: 30000 })).data;
  },
  async getPdf(submissionId, version) {
    const response = await api.get(`/submissions/${submissionId}/versions/${version}/file`, { responseType: 'blob' });
    return URL.createObjectURL(response.data);
  }
};
