import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  Briefcase,
  Calendar,
  Bell,
  ShieldAlert,
  UserCheck,
  User,
  LogOut,
  Vote,
  BookOpen,
  Share2
} from 'lucide-react';

export const AdminSidebar = ({ mobileOpen, onCloseMobile }) => {
  const navItems = [
    { to: "/admin", label: "Control Center", icon: LayoutDashboard, end: true },
    { to: "/admin/students", label: "Manage Students", icon: Users },
    { to: "/admin/faculty", label: "Manage Faculty", icon: GraduationCap },
    { to: "/admin/clubs", label: "Manage Clubs", icon: Briefcase },
    { to: "/admin/events", label: "Event Management", icon: Calendar },
    { to: "/admin/event-sharing", label: "Cross-campus sharing", icon: Share2 },
    { to: "/admin/elections", label: "Elections", icon: Vote },
    { to: "/admin/magazine", label: "Magazine", icon: BookOpen },
    { to: "/admin/notices", label: "Official Notices", icon: Bell },
    { to: "/admin/reports", label: "Reports & Complaints", icon: ShieldAlert },
    { to: "/admin/profile", label: "Admin Profile", icon: User }
  ];

  return (
    <aside className={`admin-sidebar ${mobileOpen ? 'mobile-open' : ''}`}>
      {navItems.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `admin-sidebar-item ${isActive ? 'active' : ''}`}
            onClick={onCloseMobile}
          >
            <Icon size={18} />
            <span>{item.label}</span>
          </NavLink>
        );
      })}
    </aside>
  );
};
