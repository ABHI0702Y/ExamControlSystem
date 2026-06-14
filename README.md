# Exam Control System

A full-stack web application for managing online examinations — built with Spring Boot and React.

## Features

**Admin**
- Manage users (students, teachers)
- Build question bank with subject-wise categorization
- Create and configure timed exams
- View all results and analytics

**Teacher**
- Create and manage own exams
- View student results

**Student**
- Attempt active exams with live countdown timer
- Auto-submit on time expiry
- View detailed results and scores

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, Tailwind CSS |
| Backend | Spring Boot 3.2, Spring Security |
| Auth | JWT (JSON Web Tokens) |
| Database | MySQL 8.0, Spring Data JPA |
| Desktop App | Java Swing (original version) |
| Deployment | Vercel (frontend), Render.com (backend) |

## Architecture

```
frontend/          → React + Vite (deployed on Vercel)
backend/           → Spring Boot REST API (deployed on Render)
src/               → Original Java Swing desktop app
```

**API endpoints:**
- `POST /api/auth/login` — JWT authentication
- `GET/POST/PUT/DELETE /api/users` — User management (Admin only)
- `GET/POST/PUT/DELETE /api/questions` — Question bank
- `GET/POST/PUT/DELETE /api/exams` — Exam management
- `POST /api/results/submit` — Submit exam answers
- `GET /api/results` — View results (role-filtered)

## Run Locally

**Prerequisites:** Java 17, Maven, MySQL 8, Node.js 18+

```bash
# 1. Create database
mysql -u root -p
CREATE DATABASE exam_control_db;

# 2. Backend
cd backend
# Copy and configure
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit DB credentials, then:
mvn spring-boot:run

# 3. Frontend
cd frontend
npm install
npm run dev
# Open http://localhost:5173
```

**Default login:**
- Username: `admin` | Password: `Admin@123`

## Screenshots

> Admin Dashboard → Users, Questions, Exams management
> Student Portal → Take timed exams, view results

## GitHub

[github.com/ABHI0702Y/ExamControlSystem](https://github.com/ABHI0702Y/ExamControlSystem)
