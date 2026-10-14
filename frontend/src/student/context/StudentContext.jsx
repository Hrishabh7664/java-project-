import React, { createContext, useState, useEffect } from 'react';
import { studentService } from '../services/studentService';
import { useAuth } from '../../auth/hooks/useAuth';
import { notificationService } from '../services/notificationService';
import { apiErrorMessage } from '../../services/api';

export const StudentContext = createContext(null);

export const StudentProvider = ({ children }) => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(user);
  const [loading, setLoading] = useState(true);
  const [notifications, setNotifications] = useState([]);
  const [notificationError, setNotificationError] = useState('');

  useEffect(() => {
    async function loadStudentData() {
      try {
        const prof = user?.id ? await studentService.getProfile(user.id) : user;
        setProfile(prof || user);
      } catch (err) {
        console.error('Error loading student context:', err);
      } finally {
        setLoading(false);
      }
    }
    loadStudentData();
  }, [user]);

  const refreshNotifications = async () => {
    if (!user?.id || user.role !== 'STUDENT') { setNotifications([]); return; }
    try { setNotifications(await notificationService.getNotifications()); setNotificationError(''); }
    catch (error) { setNotificationError(apiErrorMessage(error, 'Notifications could not be loaded.')); }
  };
  useEffect(() => {
    refreshNotifications();
    if (!user?.id || user.role !== 'STUDENT') return undefined;
    const timer = window.setInterval(refreshNotifications, 30000);
    return () => window.clearInterval(timer);
  }, [user]);
  const markNotificationRead = async (id) => {
    try { const updated = await notificationService.markAsRead(id); setNotifications((items) => items.map((item) => item.id === id ? { ...item, isRead: updated.isRead } : item)); }
    catch (error) { setNotificationError(apiErrorMessage(error, 'Notification could not be marked as read.')); }
  };

  return (
    <StudentContext.Provider value={{
      profile,
      loading,
      notifications,
      unreadCount: notifications.filter((n) => !n.isRead).length,
      notificationError,
      refreshNotifications,
      markNotificationRead
    }}>
      {children}
    </StudentContext.Provider>
  );
};
