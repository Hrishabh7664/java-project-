import api from '../../services/api';

const mapClub = (club) => ({ ...club, leader: club.leaderName || 'Not assigned', status: 'Published' });

export const clubManagementService = {
  async getClubs() {
    const response = await api.get('/clubs');
    return response.data.map(mapClub);
  },
  async createClub(data) {
    const response = await api.post('/clubs', {
      name: data.name, category: data.category, leaderEmail: data.leaderEmail,
      description: data.description || `${data.category} student group`, activities: data.activities || '',
      eligibility: data.eligibility || 'Open to enrolled students', department: data.department || 'All'
    });
    return mapClub(response.data);
  },
  async updateClub(id, data) { return mapClub((await api.put(`/clubs/${id}`, data)).data); },
  async deleteClub(id) { await api.delete(`/clubs/${id}`); }
};
