import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  FileCheck,
  Bell,
  User,
  PlusCircle,
  CalendarDays
} from 'lucide-react';

export const FacultySidebar = ({ mobileOpen, onCloseMobile }) => {
  const navItems = [
    { to: "/faculty", label: "Faculty Dashboard", icon: LayoutDashboard, end: true },
    { to: "/faculty/assignments", label: "Assignments", icon: FileCheck },
    { to: "/faculty/timetable", label: "Timetable & syllabus", icon: CalendarDays },
    { to: "/faculty/announcements", label: "Class Announcements", icon: Bell },
    { to: "/faculty/profile", label: "Faculty Profile", icon: User }
  ];

  return (
    <aside className={`faculty-sidebar ${mobileOpen ? 'mobile-open' : ''}`}>
      {navItems.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `faculty-sidebar-item ${isActive ? 'active' : ''}`}
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
