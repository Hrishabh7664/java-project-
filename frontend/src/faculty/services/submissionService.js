import api from '../../services/api';
export const submissionService = {
  async getSubmissions(assignmentId) { return (await api.get('/faculty/submissions', { params: assignmentId ? { assignmentId } : {} })).data; },
  async review(id, action, marks, feedback) { return (await api.post(`/faculty/submissions/${id}/review`, { action, marks: marks === '' ? null : Number(marks), feedback })).data; },
  async getPdf(id, version) {
    const response = await api.get(`/submissions/${id}/versions/${version}/file`, { responseType: 'blob' });
    return URL.createObjectURL(response.data);
  }
};
