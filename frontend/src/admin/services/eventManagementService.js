import api from '../../services/api';

const mapEvent = (event) => ({ ...event, venue: event.location, registrationsCount: null, status: 'Published' });

export const eventManagementService = {
  async getEvents() {
    const response = await api.get('/events');
    return Promise.all(response.data.map(async (event) => ({
      ...mapEvent(event),
      registrationsCount: await this.getRegistrationCount(event.id)
    })));
  },
  async getRegistrationCount(id) {
    return (await api.get(`/events/${id}/registrations/count`)).data;
  },
  async getRegistrants(id) {
    return (await api.get(`/events/${id}/registrations`)).data;
  },
  async createEvent(data) {
    const response = await api.post('/events', {
      title: data.title,
      organizer: data.organizer,
      date: data.date,
      time: data.time || '10:00 AM',
      location: data.venue,
      category: data.category || 'Campus'
    });
    return mapEvent(response.data);
  },
  async deleteEvent(id) { await api.delete(`/events/${id}`); }
};
