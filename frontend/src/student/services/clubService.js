import api from '../../services/api';
const mapClub = (club) => ({ ...club, leadership: club.leaderName ? [{ name: club.leaderName, role: 'Club leader' }] : [] });

export const clubService = {
  async getClubs(params = {}) {
    const response = await api.get('/clubs', { params });
    return response.data.map(mapClub);
  },
  async getClubById(id) {
    const response = await api.get(`/clubs/${id}`);
    return mapClub(response.data);
  },
  async getMyApplications() { return (await api.get('/student/clubs/applications')).data; },
  async apply(clubId, motivation) { return (await api.post(`/student/clubs/${clubId}/applications`, { motivation })).data; },
  async withdraw(applicationId) { return (await api.delete(`/student/clubs/applications/${applicationId}`)).data; },
  async getLeaderClubs() { return (await api.get('/student/clubs/leader')).data.map(mapClub); },
  async getClubApplications(clubId, status) { return (await api.get(`/student/clubs/leader/${clubId}/applications`, { params: { status } })).data; },
  async decide(applicationId, status, decisionNote) { return (await api.put(`/student/clubs/leader/applications/${applicationId}`, { status, decisionNote })).data; }
};
