# Campus Connect

> **A unified digital campus ecosystem connecting students, faculty, organizations, and college administration.**

---

## 📌 Project Overview

**Campus Connect** is a private, college-focused digital platform designed around the day-to-day operations of an academic institution. It unifies social engagement, academic management, organization governance, and administrative oversight into a single, cohesive campus platform.

The ecosystem combines the core strengths of:
- 💬 **Campus Social & Forums** — Reddit-style campus discussions, category boards, posts, nested comments, and voting.
- 👥 **Academic Teams & Collaboration** — Microsoft Teams-inspired faculty-created academic groups for classes and project teams.
- 🎓 **Academic & ERP Tools** — Course management, assignments, deadlines, submission feedback, and attendance tracking.
- 🤝 **Clubs & Committees** — Student organization hubs with leadership rosters, event management, and member rosters.
- 📢 **Official Notice Board** — Verified administrative circulars, exam schedules, and college-wide announcements.
- 🗳️ **Online Campus Elections** — Student council, club, and committee elections with voting and results processing.
- 🛡️ **Administration & Moderation** — Content moderation, report resolution, and administrative management.

> 📝 **Implementation Note**: This repository currently contains the initial project structure baseline. The detailed feature set described in this documentation represents the planned system vision and architecture for Campus Connect, which will be implemented iteratively.

---

## 🏛️ Core Modules

Campus Connect is structured around major interconnected modules:

### 1. 🎓 Student & Social Hub
- **Student Profiles**: Public and private campus profiles showcasing academic details (Branch, Year, Division), interests, club/committee memberships, leadership positions, and recent post activity.
- **Campus Feed & Category Forums**: Reddit-style campus feed categorized by topics (e.g., *General*, *Academics*, *Placements*, *Campus Life*, *Events*, *Technology*, *Sports*).
- **Interactive Posts**: Rich posts, nested comment threads, upvoting/downvoting, search, and category filtering.

### 2. 🤝 Organizations (Clubs & Committees)
- **Student Clubs**: Dedicated club pages (e.g., *Coding Club*, *Robotics Club*, *Sports Club*) featuring descriptions, member rosters, announcements, and events. Students can follow or request to join clubs.
- **Formal Committees**: Structured pages for administrative and student committees (e.g., *Student Council*, *Technical Committee*, *Cultural Committee*, *Anti-Ragging Committee*) with official office-bearers and notices.
- **Club/Committee Leadership**: Hierarchical leadership roles (President, Vice President, Secretary, Treasurer) managed by designated leaders or admins with scoped permissions.

### 3. 🗓️ Campus Events & Official Notice Board
- **Dedicated Events Section**: Central event hub where administrators, faculty, clubs, and committees publish hackathons, fests, workshops, and sports tournaments. Students can discover details and register.
- **Official Notice Board**: Isolated, verified channel reserved exclusively for college circulars, exam schedules, and administrative deadlines, ensuring critical notices are never lost in social feeds.

### 4. 📚 Faculty & Academic System
- **Faculty Academic Workspace**: Dashboard for faculty members to manage assigned classes, publish course announcements, and upload academic resources.
- **Teams / Groups**: Microsoft Teams-inspired academic groups created by faculty for specific classes or project teams (e.g., *SE IT Java Group*, *DBMS Project Team 4*). Groups include members, announcements, assignments, and resource sharing.
- **Assignment System**: Creation of assignments with instructions, subject tags, and deadlines. Students submit work, view status, and receive grades and feedback.
- **Attendance System**: Faculty-recorded attendance registers per class/subject. Students can track their subject-wise attendance percentages.

### 5. 🗳️ Online Campus Elections
- **Digital Voting System**: Module for conducting elections for Student Council, club leadership, and committee positions.
- **Voting Logic**: Secure rules ensuring one vote per eligible student per position, active election timeframes, and post-election result publication.
- **Workflow**: Election result $\rightarrow$ Organizational appointment $\rightarrow$ Automatic permission updates.

### 6. 🛡️ Moderation & Admin Dashboard
- **Content Moderation & Reports**: Reporting pipeline for students to report posts, comments, or users.
- **Admin Dashboard**: Master administrative panel to manage student and faculty accounts, oversee clubs/committees, manage elections, publish notices, and handle complaints.

---

## 🔐 Role & Permission Architecture

Campus Connect employs a **Dynamic Role & Permission System**. A student's core identity remains a "Student," but their memberships, appointments, and elected positions dynamically confer scoped permissions.

```
User ──► Role ──► Membership / Appointment ──► Position ──► Permissions ──► Features
```

### Key Principles:
- **Scoped Authority**: A student elected as *President of the Coding Club* receives management permissions restricted exclusively to the Coding Club. They cannot manage other clubs or publish official college notices.
- **Dynamic Progression**: Positions can be assigned via:
  - Administrative appointment
  - Successful election victory
  - Leadership assignment within a club or committee

---

## 🔄 Example Student Journey

Below is an example of how a student interacts with and progresses through the Campus Connect ecosystem:

```
[ New Student Joins Platform ]
              │
              ▼
[ Explores Feed, Registers for Events & Follows Coding Club ]
              │
              ▼
[ Submits Join Request ──► Gets Accepted as Member ]
              │
              ▼
[ Joins Faculty-Created Java Group ──► Submits Assignments & Checks Attendance ]
              │
              ▼
[ Appointed as Coding Club President ──► Profile Updates & Club Admin Panel Unlocked ]
              │
              ▼
[ Contests Student Council Election ──► Elected General Secretary ──► Council Permissions Granted ]
```

---

## 🛠️ Intended Technology Stack

The planned architecture for Campus Connect follows a clean decoupled client-server pattern:

```
┌────────────────────────────────┐
│      React.js Frontend         │
│   (HTML5 / CSS3 / JavaScript)  │
└───────────────┬────────────────┘
                │  REST API Calls (Axios)
                ▼
┌────────────────────────────────┐
│    Spring Boot Backend (Java)  │
│      (RESTful Web Services)    │
└───────────────┬────────────────┘
                │  JDBC / JPA
                ▼
┌────────────────────────────────┐
│     MySQL Database (Relational)│
└────────────────────────────────┘
```

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **Frontend** | React.js, JavaScript, HTML5, CSS3 | Single Page Application (SPA) providing a responsive user interface |
| **API Client** | Axios | Promise-based HTTP client for API requests |
| **Backend** | Java, Spring Boot | RESTful web services delivering business logic and security |
| **Database** | MySQL | Relational database management system for persistent data storage |

---

## 🎯 Project Goals

- 🌐 **Unified Campus Platform**: Consolidate disparate college channels into one seamless platform.
- 💬 **Enhanced Communication**: Bridge communication between students, faculty, clubs, and college administration.
- 📊 **Centralized Academic Hub**: Simplify assignment tracking, submission feedback, attendance records, and group work.
- 🔑 **Fine-Grained Access Control**: Implement a flexible permission model matching real-world campus roles.
- 🗳️ **Transparent Governance**: Digitize campus elections and official administrative notice distribution.

---

## 🚀 Future Scope

Future iterations of Campus Connect may explore:
- 🔔 **Real-Time Notifications**: Instant updates for announcements, assignments, and election results.
- 💬 **Real-Time Messaging**: Direct messaging and team chat capabilities using WebSockets.
- 📱 **Mobile Application**: Cross-platform mobile client (React Native / Flutter).
- 📁 **Cloud Asset Storage**: Integration with cloud storage for assignment resource uploads.
- 📈 **Campus Analytics**: Insights into student engagement, attendance trends, and event participation.
- 🔌 **ERP Integration**: Connectors to integrate with existing institutional ERP systems.

---

## 💻 Running the Application & Presentation Demo

### 1. 🎓 Standalone Presentation Demo (`demo/`)
A self-contained, presentation-ready build designed for demonstration and localhost reviews without backend dependencies or login barriers.

```bash
# Navigate to the demo directory
cd demo

# Install dependencies
npm install

# Start the interactive presentation server
npm run dev
```

Open `http://localhost:3000` to interactively switch between **Student Portal**, **Faculty Workspace**, and **Admin Control Center**.

---

### 2. ⚡ Real Frontend Application (`frontend/`)
The production client codebase prepared for future Spring Boot REST API + MySQL backend integration.

```bash
# Navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

### 3. 🔌 Spring Boot REST API (`backend/`)
The backend exposes the clubs resource at `http://localhost:8080/api/clubs`.
Create the `campus_connect` MySQL database and provide the credentials through
environment variables before starting it. In PowerShell:

```bash
cd backend
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS campus_connect;"
```

Then start the backend:

```bash
cd backend
mvn spring-boot:run
```

Available REST operations:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/clubs` | List all clubs |
| `GET` | `/api/clubs/{id}` | Get one club |
| `POST` | `/api/clubs` | Create a club; `id` is generated when omitted |
| `PUT` | `/api/clubs/{id}` | Replace an existing club |
| `DELETE` | `/api/clubs/{id}` | Delete a club |

Example request:

```json
{
  "name": "Coding Club",
  "category": "Technical",
  "description": "A community for programming and projects.",
  "membersCount": 0
}
```