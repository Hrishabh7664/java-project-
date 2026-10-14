import api from '../../services/api';

const statusLabels = {
  SUBMITTED: 'Submitted',
  PENDING: 'Submitted',
  UNDER_REVIEW: 'Under review',
  INVESTIGATING: 'Investigating',
  RESOLVED: 'Resolved',
  REJECTED: 'Rejected'
};

const statusValues = {
  Submitted: 'SUBMITTED',
  'Under review': 'UNDER_REVIEW',
  Investigating: 'INVESTIGATING',
  Resolved: 'RESOLVED',
  Rejected: 'REJECTED'
};

const mapReport = (report) => ({
  ...report,
  subject: report.title,
  dateSubmitted: '—',
  identityMode: report.anonymous ? 'Anonymous' : 'Identified',
  reporterName: report.anonymous ? 'Protected' : report.submittedBy,
  status: statusLabels[report.status] || report.status || 'Submitted'
});

export const reportService = {
  async getReports() {
    const response = await api.get('/complaints');
    return response.data.map(mapReport);
  },

  async getReportById(id) {
    const response = await api.get(`/complaints/${id}`);
    return mapReport(response.data);
  },

  async updateReport(id, updateData) {
    const response = await api.put(`/complaints/${id}`, {
      title: updateData.title,
      description: updateData.description,
      category: updateData.category,
      status: statusValues[updateData.status] || updateData.status,
      submittedBy: updateData.submittedBy,
      anonymous: updateData.anonymous
    });
    return mapReport(response.data);
  },
  async getModerationReports(){return (await api.get('/admin/moderation/reports')).data;},
  async resolveModerationReport(id,action,reason){return (await api.post(`/admin/moderation/reports/${id}/resolve`,{action,reason})).data;},
  async getModerationContent(type,id){return (await api.get(`/admin/moderation/content/${type}/${id}`)).data;}
};
