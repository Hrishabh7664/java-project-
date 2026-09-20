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

---

## 💻 Full Stack Architecture (Experiment 05 Integration)

```
┌────────────────────────────────┐
│      React.js Frontend         │
│   (HTML5 / CSS3 / JavaScript)  │
└───────────────┬────────────────┘
                │  REST API Calls (Axios / Fetch)
                ▼
┌────────────────────────────────┐
│     REST Controllers           │
│     (@RestController)          │
└───────────────┬────────────────┘
                │  Injects Service
                ▼
┌────────────────────────────────┐
│       Service Layer            │
│     (@Service Components)      │
└───────────────┬────────────────┘
                │  Invokes Repositories
                ▼
┌────────────────────────────────┐
│     Spring Data JPA Repos      │
│  (extends JpaRepository<T, ID>)│
└───────────────┬────────────────┘
                │  Hibernate ORM Mapping
                ▼
┌────────────────────────────────┐
│      MySQL Database            │
│   (Database: campus_connect)   │
└────────────────────────────────┘
```

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **Frontend** | React.js, JavaScript, HTML5, CSS3 | Single Page Application (SPA) providing a responsive user interface |
| **Backend Framework** | Java 17, Spring Boot 3.3.3 | RESTful web services delivering business logic and CRUD endpoints |
| **Persistence** | Spring Data JPA, Hibernate ORM | Object-Relational Mapping (ORM) and data repositories |
| **Database** | MySQL 8.0+ | Relational database persistent data storage (`campus_connect`) |

---

## 🗄️ Experiment 05: Spring Boot + Spring Data JPA + MySQL

### Prerequisites
- Java 17 or higher
- Apache Maven 3.8+
- MySQL Server 8.0+

### Step 1: Create Database in MySQL
```sql
CREATE DATABASE campus_connect;
```

### Step 2: Configure Credentials (`backend/src/main/resources/application.properties`)
```properties
spring.application.name=campus-connect-api

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/campus_connect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

server.port=8080
```

### Step 3: Persistent JPA Entities & Database Tables
| Entity | MySQL Table Name | Primary Key (`@Id`) | Key Fields |
| :--- | :--- | :--- | :--- |
| `Student` | `students` | `@GeneratedValue Long id` | name, email, department, year, division, bio |
| `Post` | `posts` | `@GeneratedValue Long id` | authorName, title, content, category, createdAt, voteCount |
| `Comment` | `comments` | `@GeneratedValue Long id` | postId, authorName, content, createdAt |
| `Club` | `clubs` | `@GeneratedValue Long id` | name, category, logo, cover, description, department, president, membersCount |
| `Event` | `events` | `@GeneratedValue Long id` | title, description, organizer, date, time, location, category |
| `Notice` | `notices` | `@GeneratedValue Long id` | title, content, author, publishedAt, category |
| `Assignment` | `assignments` | `@GeneratedValue Long id` | title, description, subject, facultyName, dueDate, status |
| `Election` | `elections` | `@GeneratedValue Long id` | title, position, startDate, endDate, status |
| `Complaint` | `complaints` | `@GeneratedValue Long id` | title, description, category, status, submittedBy, anonymous |

---

## 🔌 REST API Endpoints & CRUD Operations

### System Health
- `GET /api/health` — Returns status UP and database connection info.

### Resource CRUD Endpoints
All resources (`/api/students`, `/api/posts`, `/api/comments`, `/api/clubs`, `/api/events`, `/api/notices`, `/api/assignments`, `/api/elections`, `/api/complaints`) support full CRUD:

| Operation | HTTP Method | Endpoint | Request Body / Description |
| :--- | :--- | :--- | :--- |
| **CREATE** | `POST` | `/api/{resource}` | JSON payload containing entity properties |
| **READ ALL** | `GET` | `/api/{resource}` | Fetches all entities stored in MySQL |
| **READ ONE** | `GET` | `/api/{resource}/{id}` | Fetches single entity by primary key ID |
| **UPDATE** | `PUT` | `/api/{resource}/{id}` | Replaces/updates existing entity record in MySQL |
| **DELETE** | `DELETE` | `/api/{resource}/{id}` | Removes entity from MySQL by primary key ID |

---

## 🔍 Verification via MySQL Workbench / SQL Commands

After running the backend, open MySQL Workbench or MySQL CLI and run:

```sql
SHOW DATABASES;
USE campus_connect;
SHOW TABLES;

-- Inspect initial seed data created automatically by DataInitializer
SELECT * FROM students;
SELECT * FROM posts;
SELECT * FROM clubs;
SELECT * FROM events;
SELECT * FROM notices;
```

---

## 💻 Running the Application & Presentation Demo

### 1. 🎓 Standalone Presentation Demo (`demo/`)
A self-contained, presentation-ready build designed for demonstration and localhost reviews without backend dependencies or login barriers.

```bash
cd demo
npm install
npm run dev
```

### 2. ⚡ Real Frontend Application (`frontend/`)
The client codebase configured for REST API communication.

```bash
cd frontend
npm install
npm run dev
```

### 3. 🔌 Spring Boot Backend (`backend/`)
Run automated tests and launch Spring Boot backend:

```bash
cd backend
mvn clean test
mvn spring-boot:run
```