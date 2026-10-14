import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { StudentNavbar } from '../components/Navbar';
import { StudentSidebar } from '../components/Sidebar';
import { StudentProvider } from '../context/StudentContext';
import '../styles/student.css';
import '../styles/feed.css';

export const StudentLayout = () => {
  const [mobileSidebar, setMobileSidebar] = useState(false);

  return (
    <StudentProvider>
      <div className="student-app-shell">
        <StudentNavbar mobileOpen={mobileSidebar} onToggleSidebar={() => setMobileSidebar(!mobileSidebar)} />

        <div className="student-main-layout">
          {mobileSidebar && <button className="workspace-backdrop" type="button" aria-label="Close navigation" onClick={() => setMobileSidebar(false)} />}
          <StudentSidebar mobileOpen={mobileSidebar} onCloseMobile={() => setMobileSidebar(false)} />
          <main className="student-content-area">
            <Outlet />
          </main>
        </div>
      </div>
    </StudentProvider>
  );
};
