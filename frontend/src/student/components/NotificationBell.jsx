import React, { useState } from 'react';
import { Bell, Check } from 'lucide-react';
import { useStudent } from '../hooks/useStudent';
import { useNavigate } from 'react-router-dom';

export const NotificationBell = () => {
  const { notifications, unreadCount, markNotificationRead, notificationError } = useStudent();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  return (
    <div style={{ position: 'relative' }}>
      <button className="notif-bell-btn" onClick={() => setOpen(!open)} aria-label="Notifications">
        <Bell size={20} />
        {unreadCount > 0 && <span className="notif-badge">{unreadCount}</span>}
      </button>

      {open && (
        <div className="notif-dropdown">
          <div className="notif-header">
            <span>Notifications</span>
            <span style={{ fontSize: '0.78rem', color: 'var(--primary-light)', cursor: 'pointer' }}>
              {unreadCount} unread
            </span>
          </div>

          {notificationError && <div role="alert" style={{ padding: '.75rem', color: 'var(--error)' }}>{notificationError}</div>}
          <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
            {notifications.length === 0 ? (
              <div style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.88rem' }}>
                No notifications yet.
              </div>
            ) : (
              notifications.map((n) => (
                <div
                  key={n.id}
                  className={`notif-item ${!n.isRead ? 'unread' : ''}`}
                  onClick={() => {
                    markNotificationRead(n.id);
                    setOpen(false);
                    if (n.link) navigate(n.link);
                  }}
                >
                  <div style={{ fontWeight: '600', fontSize: '0.85rem', color: 'var(--text-primary)' }}>{n.title}</div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '2px' }}>{n.message}</div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', marginTop: '4px' }}>{n.timeAgo}</div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};
