import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/context/AuthContext';
import { LandingPage } from './pages/LandingPage';
import { Login } from './auth/pages/Login';

// Student Module Layout & Pages
import { StudentLayout } from './student/pages/StudentLayout';
import { StudentDashboard } from './student/pages/StudentDashboard';
import { StudentProfile } from './student/pages/StudentProfile';
import { Feed } from './student/pages/Feed';
import { Forums } from './student/pages/Forums';
import { PostDetails } from './student/pages/PostDetails';
import { Clubs } from './student/pages/Clubs';
import { ClubDetails } from './student/pages/ClubDetails';
import { Committees } from './student/pages/Committees';
import { CommitteeDetails } from './student/pages/CommitteeDetails';
import { Events } from './student/pages/Events';
import { EventDetails } from './student/pages/EventDetails';
import { Notices } from './student/pages/Notices';
import { Elections } from './student/pages/Elections';
import { ElectionDetails } from './student/pages/ElectionDetails';
import { MyAssignments } from './student/pages/MyAssignments';
import { AssignmentDetails } from './student/pages/AssignmentDetails';
import { Attendance } from './student/pages/Attendance';
import { Teams } from './student/pages/Teams';
import { TeamDetails } from './student/pages/TeamDetails';
import { Complaints } from './student/pages/Complaints';
import { MyOrganizations } from './student/pages/MyOrganizations';
import { MyActivity } from './student/pages/MyActivity';

// Admin Module Layout & Pages
import { AdminLayout } from './admin/pages/AdminLayout';
import { AdminDashboard } from './admin/pages/AdminDashboard';
import { ManageStudents } from './admin/pages/ManageStudents';
import { ManageFaculty } from './admin/pages/ManageFaculty';
import { ManageClubs } from './admin/pages/ManageClubs';
import { ManageCommittees } from './admin/pages/ManageCommittees';
import { ManageOrganizations } from './admin/pages/ManageOrganizations';
import { ManageAppointments } from './admin/pages/ManageAppointments';
import { ManageEvents } from './admin/pages/ManageEvents';
import { ManageNotices } from './admin/pages/ManageNotices';
import { ManageElections } from './admin/pages/ManageElections';
import { CandidateManagement } from './admin/pages/CandidateManagement';
import { ElectionResults } from './admin/pages/ElectionResults';
import { ManageReports } from './admin/pages/ManageReports';
import { ReportDetails } from './admin/pages/ReportDetails';
import { AdminProfile } from './admin/pages/AdminProfile';

// Faculty Module Layout & Pages
import { FacultyLayout } from './faculty/pages/FacultyLayout';
import { FacultyDashboard } from './faculty/pages/FacultyDashboard';
import { MyClasses } from './faculty/pages/MyClasses';
import { ClassDetails } from './faculty/pages/ClassDetails';
import { Assignments } from './faculty/pages/Assignments';
import { CreateAssignment } from './faculty/pages/CreateAssignment';
import { AssignmentDetailsFaculty } from './faculty/pages/AssignmentDetails';
import { Submissions } from './faculty/pages/Submissions';
import { AttendanceFaculty } from './faculty/pages/Attendance';
import { TakeAttendance } from './faculty/pages/TakeAttendance';
import { TeamsFaculty } from './faculty/pages/Teams';
import { CreateTeamFaculty } from './faculty/pages/CreateTeam';
import { TeamDetailsFaculty } from './faculty/pages/TeamDetails';
import { AnnouncementsFaculty } from './faculty/pages/Announcements';
import { FacultyProfile } from './faculty/pages/FacultyProfile';

// Campus Connect Full Stack Components (Protected)
import Navbar from './components/Navbar';
import CampusLogin from './components/Login';
import Dashboard from './components/Dashboard';
import PostList from './components/PostList';
import EventList from './components/EventList';
import ClubList from './components/ClubList';
import AssignmentList from './components/AssignmentList';
import ElectionList from './components/ElectionList';
import ComplaintForm from './components/ComplaintForm';

// Experiment 3 & 6 Banking & Protected Route Integration
import BankingDashboard from './banking/BankingDashboard';
import ProtectedRoute from './components/ProtectedRoute';

import './styles/global.css';

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Landing & Authentication */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<CampusLogin />} />
          <Route path="/campus-login" element={<Login />} />

          {/* Campus Connect Protected Application Routes */}
          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/posts" element={<ProtectedRoute><PostList /></ProtectedRoute>} />
          <Route path="/events" element={<ProtectedRoute><EventList /></ProtectedRoute>} />
          <Route path="/clubs" element={<ProtectedRoute><ClubList /></ProtectedRoute>} />
          <Route path="/assignments" element={<ProtectedRoute><AssignmentList /></ProtectedRoute>} />
          <Route path="/elections" element={<ProtectedRoute><ElectionList /></ProtectedRoute>} />
          <Route path="/complaints" element={<ProtectedRoute><ComplaintForm /></ProtectedRoute>} />

          {/* Experiment 03 & 06: Banking Dashboard Protected with JWT */}
          <Route path="/banking" element={<ProtectedRoute><BankingDashboard /></ProtectedRoute>} />

          {/* Student Module Routes */}
          <Route path="/student" element={<StudentLayout />}>
            <Route index element={<StudentDashboard />} />
            <Route path="profile" element={<StudentProfile />} />
            <Route path="feed" element={<Feed />} />
            <Route path="forums" element={<Forums />} />
            <Route path="forums/:id" element={<PostDetails />} />
            <Route path="clubs" element={<Clubs />} />
            <Route path="clubs/:id" element={<ClubDetails />} />
            <Route path="committees" element={<Committees />} />
            <Route path="committees/:id" element={<CommitteeDetails />} />
            <Route path="events" element={<Events />} />
            <Route path="events/:id" element={<EventDetails />} />
            <Route path="notices" element={<Notices />} />
            <Route path="elections" element={<Elections />} />
            <Route path="elections/:id" element={<ElectionDetails />} />
            <Route path="assignments" element={<MyAssignments />} />
            <Route path="assignments/:id" element={<AssignmentDetails />} />
            <Route path="attendance" element={<Attendance />} />
            <Route path="teams" element={<Teams />} />
            <Route path="teams/:id" element={<TeamDetails />} />
            <Route path="complaints" element={<Complaints />} />
            <Route path="organizations" element={<MyOrganizations />} />
            <Route path="activity" element={<MyActivity />} />
          </Route>

          {/* Admin Control Center Routes */}
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="students" element={<ManageStudents />} />
            <Route path="faculty" element={<ManageFaculty />} />
            <Route path="clubs" element={<ManageClubs />} />
            <Route path="committees" element={<ManageCommittees />} />
            <Route path="organizations" element={<ManageOrganizations />} />
            <Route path="appointments" element={<ManageAppointments />} />
            <Route path="events" element={<ManageEvents />} />
            <Route path="notices" element={<ManageNotices />} />
            <Route path="elections" element={<ManageElections />} />
            <Route path="elections/candidates" element={<CandidateManagement />} />
            <Route path="elections/results" element={<ElectionResults />} />
            <Route path="reports" element={<ManageReports />} />
            <Route path="reports/:id" element={<ReportDetails />} />
            <Route path="profile" element={<AdminProfile />} />
          </Route>

          {/* Faculty Workspace Routes */}
          <Route path="/faculty" element={<FacultyLayout />}>
            <Route index element={<FacultyDashboard />} />
            <Route path="classes" element={<MyClasses />} />
            <Route path="classes/:id" element={<ClassDetails />} />
            <Route path="assignments" element={<Assignments />} />
            <Route path="assignments/create" element={<CreateAssignment />} />
            <Route path="assignments/:id" element={<AssignmentDetailsFaculty />} />
            <Route path="assignments/:id/submissions" element={<Submissions />} />
            <Route path="submissions" element={<Submissions />} />
            <Route path="attendance" element={<AttendanceFaculty />} />
            <Route path="attendance/take" element={<TakeAttendance />} />
            <Route path="attendance/history" element={<AttendanceFaculty />} />
            <Route path="teams" element={<TeamsFaculty />} />
            <Route path="teams/create" element={<CreateTeamFaculty />} />
            <Route path="teams/:id" element={<TeamDetailsFaculty />} />
            <Route path="teams/:id/members" element={<TeamDetailsFaculty />} />
            <Route path="teams/:id/discussions" element={<TeamDetailsFaculty />} />
            <Route path="announcements" element={<AnnouncementsFaculty />} />
            <Route path="profile" element={<FacultyProfile />} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
