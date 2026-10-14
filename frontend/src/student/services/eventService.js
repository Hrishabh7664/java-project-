import api from '../../services/api';

const mapEvent = (event) => ({ ...event, venue: event.location });

export const eventService = {
  async getEvents() {
    const response = await api.get('/events');
    return response.data.map(mapEvent);
  },

  async getSharedEvents(){return (await api.get('/events/shared')).data;},

  async getEventById(id) {
    const response = await api.get(`/events/${id}`);
    return mapEvent(response.data);
  },

  async register(id) {
    const response = await api.post(`/events/${id}/registrations`);
    return response.data;
  },

  async cancelRegistration(id) {
    await api.delete(`/events/${id}/registrations/me`);
  },

  async getMyRegistrations() {
    const response = await api.get('/events/registrations/me');
    return response.data;
  },

  async getRegistrationCount(id) {
    const response = await api.get(`/events/${id}/registrations/count`);
    return response.data;
  }
};
