import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  MessageSquare,
  Compass,
  Users,
  Building2,
  Calendar,
  Bell,
  Vote,
  BookOpen,
  UserCheck,
  GraduationCap,
  ShieldAlert,
  Briefcase,
  Activity,
  User,
  CalendarDays,
  ClipboardList,
  ShieldCheck
} from 'lucide-react';

export const StudentSidebar = ({ mobileOpen, onCloseMobile }) => {
  const navItems = [
    { to: "/student", label: "Dashboard", icon: LayoutDashboard, end: true },
    { to: "/student/feed", label: "Campus Feed", icon: MessageSquare },
    { to: "/student/forums", label: "Category Forums", icon: Compass },
    { to: "/student/clubs", label: "Student Clubs", icon: Users },
    { to: "/student/clubs/applications", label: "Club Applications", icon: ClipboardList },
    { to: "/student/clubs/manage", label: "Club Join Applications", icon: ShieldCheck },
    { to: "/student/events", label: "Campus Events", icon: Calendar },
    { to: "/student/magazine", label: "Campus Magazine", icon: BookOpen },
    { to: "/student/elections", label: "Elections", icon: Vote },
    { to: "/student/notices", label: "Official Notices", icon: Bell },
    { to: "/student/assignments", label: "My Assignments", icon: BookOpen },
    { to: "/student/attendance", label: "My Attendance", icon: UserCheck },
    { to: "/student/timetable", label: "My Timetable", icon: CalendarDays },
    { to: "/student/complaints", label: "Complaints Portal", icon: ShieldAlert },
    { to: "/student/profile", label: "Student Profile", icon: User }
  ];

  return (
    <aside className={`student-sidebar ${mobileOpen ? 'mobile-open' : ''}`}>
      {navItems.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}
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
