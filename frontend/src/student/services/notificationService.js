import api from '../../services/api';
export const notificationService = {
  async getNotifications() { return (await api.get('/student/notifications')).data.map((n) => ({ ...n, timeAgo: new Date(n.createdAt).toLocaleString() })); },
  async markAsRead(id) { return (await api.put(`/student/notifications/${id}/read`)).data; }
};
