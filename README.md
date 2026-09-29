> **Account:** ChauPX

# 🎯 Quiz Lite

A lightweight, fast, scalable, and AI-powered Quiz Application.

## 🚀 Overview
**Quiz Lite** is a comprehensive quiz management system designed for high performance and seamless user experience. It features AI-powered quiz generation, event-driven grading, real-time notifications, and high-throughput auto-saving mechanisms to handle large-scale concurrent users.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3 (Web, Data JPA, Security)
* **Database:** PostgreSQL (Primary Data & JSONB storage), Redis (In-memory Cache for high-throughput)
* **Message Broker:** RabbitMQ (Asynchronous processing and grading)
* **Frontend:** React + Vite (Modern UI with Glassmorphism and Dark Mode)
* **AI Integration:** Google Gemini (Generative Language API)
* **Security:** Stateless JWT Authentication & Role-Based Access Control (RBAC)

## ✨ Core Features
* 🔐 **User Authentication:** Secure Login and Registration using Stateless JWT.
* 🤖 **AI-Powered Generation:** Admins can instantly generate hundreds of quiz questions from just a topic name using Gemini, with automatic retry mechanisms for API rate limits.
* ⏳ **Time Limits & Auto-Submit:** Configurable countdown timers that automatically lock and submit the exam when time runs out.
* 💾 **Ultra-fast Auto-Save:** User answers are cached in a Redis Hash with O(1) complexity during the exam to prevent data loss without overloading the main database.
* 📊 **Event-Driven Grading:** Exam submissions are pushed to RabbitMQ for asynchronous grading, ensuring the system never hangs even with thousands of concurrent submissions.
* 🔔 **Live Notifications:** WebSocket + STOMP integration to push grading results to users in real-time.
* 🔍 **Detailed Reviews:** Admins can review the exact interactive history (evidence) of user submissions.

## 📂 Project Structure
* `src/main/java/com/quiz/` - Main backend source code.
* `frontend/` - React frontend application.
* `docker-compose.yml` - Quick infrastructure setup (Postgres, Redis, RabbitMQ).

## 🏃‍♂️ Getting Started

### 1. Prerequisites
* Java 17+
* Node.js & npm (for frontend)
* Docker & Docker Compose (optional but recommended for services)

### 2. Infrastructure Setup (Docker)
Run the following command to start PostgreSQL, Redis, and RabbitMQ:
```bash
docker-compose up -d
```

### 3. Run the Backend
```bash
./gradlew bootRun
```
The backend server will start on `http://localhost:8080`.

### 4. Run the Frontend
```bash
cd frontend
npm install
npm run dev
```
The frontend will be accessible at `http://localhost:5173`.

