vercel deployment

https://java-project-ten-sage.vercel.app/


# Campus Connect

Campus Connect is a React single page app backed by Spring Boot REST APIs and MySQL. Student registration and login use BCrypt password hashes and signed JWTs. Faculty and administrator accounts are provisioned by an operator; public registration can only create student accounts.

## Architecture

- **Frontend:** React, Vite, Axios, React Router. The shared API client attaches the stored bearer token and clears an expired session on HTTP 401.
- **Backend:** Java 17, Spring Boot 3, Spring Security, JPA/Hibernate. Controllers expose auth, students, posts, comments, clubs, events, notices, assignments, elections, and complaints.
- **Database:** MySQL 8 with JPA schema updates. Compose persists data in the `mysql_data` named volume.
- **Authentication:** `POST /api/auth/register` creates a student, `POST /api/auth/login` returns a JWT, and `GET /api/auth/me` validates it. Protected requests use `Authorization: Bearer <token>`. Roles are `STUDENT`, `FACULTY`, and `ADMIN`.

## Run with Docker Compose (recommended)

Requirements: Docker Desktop or Docker Engine with the Compose plugin.

```bash
cp .env.example .env
```

Edit `.env` and replace the database passwords and JWT signing secret with private values. Then start the services:

```bash
docker compose up --build -d
docker compose ps
```

Open <http://localhost:3000>. Compose waits for MySQL and the backend health checks before starting dependent services. The frontend Nginx server proxies `/api` to Spring Boot. Stop the services with `docker compose down`; the database volume remains. `docker compose down -v` also deletes the persisted database.

### Environment variables

| Variable | Purpose |
| --- | --- |
| `MYSQL_PASSWORD` | MySQL application user's password |
| `MYSQL_ROOT_PASSWORD` | MySQL root password |
| `JWT_SECRET` | Private signing key (use a long random value) |
| `APP_PORT` | Host port for the frontend (default `3000`) |
| `DEMO_ACCOUNTS_ENABLED` | Set to `true` to provision optional staff accounts at startup |
| `DEMO_ADMIN_EMAIL`, `DEMO_ADMIN_PASSWORD`, `DEMO_ADMIN_NAME` | Optional administrator account settings |
| `DEMO_FACULTY_EMAIL`, `DEMO_FACULTY_PASSWORD`, `DEMO_FACULTY_NAME` | Optional faculty account settings |

Staff account email addresses must use `@tsdcem.ac.in`. Set these passwords in your local `.env`; the project does not ship default demo credentials. When staff provisioning is enabled, the accounts are created only if their email does not already exist.

## Run services locally

Create a MySQL database named `campus_connect`, then configure the backend environment:

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/campus_connect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
$env:DB_USERNAME='campus'
$env:DB_PASSWORD='your-local-password'
$env:JWT_SECRET='your-private-signing-secret-with-at-least-32-characters'
cd backend
mvn spring-boot:run
```

In another terminal:

```bash
cd frontend
npm ci
npm run dev
```

Vite runs at <http://localhost:3000> and calls the API at `http://localhost:8080/api` by default. Override `VITE_API_BASE_URL` when the API uses another address. Set `CORS_ALLOWED_ORIGINS` on the backend if the frontend origin differs from `http://localhost:3000` or `http://localhost:5173`.

## Workflows

- Students can register, sign in, update their profile, read and create posts, comment/vote where permitted, browse persisted events/clubs/notices, view class assignments and timetables, submit PDF coursework, and see faculty review results.
- Faculty can see their linked timetable and syllabus, open attendance from a scheduled lecture, create class-scoped assignments, review embedded PDF submissions, request revisions, approve work, and grade with feedback.
- Administrators can manage students, faculty, clubs, events, and notices. Staff roles are assigned by provisioning and cannot be self-selected during public signup.
- Logout clears local session state. A rejected/expired token clears the client session and returns protected routes to sign-in.

Some unrelated prototype pages for elections and teams still contain demo data and are not represented as persisted workflows.

### Demo academic structure and attendance

The backend seeds repeatable demo records for IT, CE, ECS, and ME, with FE and SE classes, Division A, Batch A1, semester subjects, faculty profiles, Monday lecture slots, and enrolled demo students. Demo department labels are explicitly marked in the UI and can be configured with CAMPUS_DEMO_DEPARTMENT_IT, CAMPUS_DEMO_DEPARTMENT_CE, CAMPUS_DEMO_DEPARTMENT_ECS, and CAMPUS_DEMO_DEPARTMENT_ME (Spring property equivalents are campus.demo.department.*). Seed rows are inserted only when their stable identifiers are absent.

To exercise faculty attendance, provision a faculty account with DEMO_ACCOUNTS_ENABLED=true and set DEMO_FACULTY_EMAIL, DEMO_FACULTY_PASSWORD, and optionally DEMO_FACULTY_NAME; that account is assigned the demo IT classes. New student accounts that select a seeded department, year, division, and batch are enrolled in the matching demo class. Faculty endpoints are under /api/faculty/attendance; the signed-in student's persisted summary is GET /api/student/attendance. Demo names, timetable, subjects, and departments are illustrative configuration, not verified institutional records.

### Assignment PDF storage

PDFs are stored under `./uploads/assignments` by default (ignored by Git). Set `ASSIGNMENT_STORAGE_DIR` to another persistent, writable local directory when needed. The database stores opaque file keys and version metadata, not absolute paths. Local filesystem storage is single-instance and is not automatically replicated or backed up; retain the configured directory across app restarts. Files are limited to 10 MB and validated using the extension and PDF signature.

## API examples

```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Example Student","email":"student@tsdcem.ac.in","password":"ChangeThis123"}'
```

Use the returned token on protected API requests:

```bash
curl http://localhost:3000/api/auth/me -H 'Authorization: Bearer YOUR_TOKEN'
```

## Tests and builds

```bash
cd backend
mvn test
cd ../frontend
npm ci
npm run build
cd ..
docker compose config --quiet
docker compose build
```

The backend integration tests cover student registration, BCrypt-backed login, JWT protected requests, role authorization, and post CRUD. Compose health checks cover database and HTTP service readiness.
