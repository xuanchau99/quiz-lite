# 🎯 Quiz Lite

A lightweight, fast, and scalable Quiz Application.

## 🚀 Overview
**Quiz Lite** is a comprehensive quiz management system designed for high performance and seamless user experience. It allows users to take quizzes, tracks exam results, and provides real-time notifications. 

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot (Web, Data JPA, Security)
* **Database:** PostgreSQL (Primary Data), Redis (Caching)
* **Message Broker:** RabbitMQ (Asynchronous processing)
* **Frontend:** React + Vite (Fast and modern UI)
* **Security:** JWT Authentication & Role-Based Access Control (RBAC)

## ✨ Core Features
* 🔐 **User Authentication:** Secure Login and Registration using JWT.
* 📝 **Quiz Engine:** Dynamic quiz taking with time limits.
* 📊 **Exam Results:** Real-time scoring and historical tracking.
* 🔔 **Live Notifications:** WebSocket + RabbitMQ integration for real-time alerts.
* 💾 **Auto-Save:** Redis-backed auto-saving for ongoing quizzes to prevent data loss.

## 📂 Project Structure
* `src/main/java/com/quiz/` - Main backend source code.
* `frontend/` - React frontend application.
* `guide/` - Detailed documentation and architecture flowcharts (HTML/MD).
* `docker-compose.yml` - Quick infrastructure setup (Postgres, Redis, RabbitMQ).

## 🏃‍♂️ Getting Started

### 1. Prerequisites
* Java 17+
* Node.js & npm (for frontend)
* Docker & Docker Compose (optional but recommended for services)

### 2. Infrastructure Setup (Docker)
Run the following command to start PostgreSQL, Redis, and RabbitMQ:
\`\`\`bash
docker-compose up -d
\`\`\`

### 3. Run the Backend
\`\`\`bash
./gradlew bootRun
\`\`\`
The backend server will start on `http://localhost:8080`.

### 4. Run the Frontend
\`\`\`bash
cd frontend
npm install
npm run dev
\`\`\`
The frontend will be accessible at `http://localhost:5173`.

## 📚 Documentation
For more detailed architectural flows and system designs, please check the `/guide` folder.
