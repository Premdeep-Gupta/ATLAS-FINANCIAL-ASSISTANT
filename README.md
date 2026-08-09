# Atlas AI Financial Assistant 🚀

> **Enterprise-Grade AI Financial Intelligence Platform & Telegram Workspace Assistant**

Built with **Java 21+**, **Spring Boot 3.3.2**, **Google GenAI Java SDK (Gemini 2.5)**, and a **Glassmorphic Financial Web Dashboard**.

---

## 🌟 Overview

Finance professionals spend an enormous amount of time switching between stock screeners, news sites, SEC filings, email threads, and spreadsheets. **Atlas AI Financial Assistant** synthesizes real-time market data, company fundamentals, document intelligence, and workspace productivity tools into a unified, conversational assistant accessible via **Telegram** and a **Web Workspace Dashboard**.

---

## 🎯 Architecture & Design Highlights

- **Spring Boot 3 Backend Platform**: Modular, scalable architecture leveraging Spring Data JPA, SQLite, and Spring Scheduler.
- **Google GenAI Java SDK (Gemini 2.5)**: Powered by Google's native GenAI Java library for high-speed financial reasoning, multi-turn tool declarations, audio transcriptions, and document context processing.
- **Telegram Bot Integration**: Conversational onboarding, document Q&A, voice note processing, and natural chat.
- **Premium Glassmorphic Web Dashboard**: An HTML5 / CSS3 / ES6 JS single-page web app served directly by Spring Boot static resources, featuring dark-mode styling, real-time market indices, interactive Chart.js graphs, an AI chat terminal, document drag-and-drop uploader, and workspace tools.
- **Zero External Frontend Build Tools Needed**: Runs out-of-the-box with zero Node/npm dependencies.

---

## ⚡ Key Features

1. **Natural Conversational Intelligence**: Responds like a senior financial analyst. Clarifies ambiguous queries, highlights margin trends, and delivers concise analyst takeaways.
2. **Conversational Onboarding**: Gradually learns user role, watchlists, and briefing schedules without cumbersome forms.
3. **Automated Daily Briefings**: Background cron jobs deliver morning briefings containing broad market indices (S&P 500, Nasdaq, Dow) and watchlist updates.
4. **Document Intelligence**: Upload 10-Ks, annual reports, PDFs, or CSV/Excel spreadsheets to extract AI executive summaries, risk factors, and financial metrics.
5. **Workspace Productivity Integrations**: Search Gmail inbox messages, schedule Google Calendar team syncs, and inspect Google Sheets KPI models.
6. **Market Volatility Price Alerts**: Monitors watchlist stock movements every 5 minutes and triggers notifications when a stock moves > 5%.

---

## 💻 Quick Start & Running Instructions

### Prerequisites
- **Java 21+** installed (`java -version`).
- Telegram Bot Token & Gemini API Key (optional, mock mode supported automatically if keys are absent).

### Launching the Application

Execute the startup script from the root directory:

```bash
./run.sh
```

Or manually navigate to the `backend/` directory and execute:

```bash
cd backend
./mvnw spring-boot:run
```

Once running, access the **Web Workspace Dashboard** in your browser at:
👉 **`http://localhost:8080/`**

---

## 🔑 Environment Variables Configuration

Create or edit the `.env` file in the project root:

```env
# Telegram Bot Configuration
TELEGRAM_BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN

# Google Gemini API Configuration
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

---

## 📁 Repository Structure

```
atlas-financial-assistant/
├── backend/
│   ├── src/main/java/com/atlas/financial/
│   │   ├── controller/      # DashboardApiController REST endpoints
│   │   ├── model/           # JPA Entities (User, Watchlist, ConversationHistory)
│   │   ├── repository/      # Spring Data Repositories
│   │   ├── service/         # AIService, FinancialService, TelegramBotService, DocumentService
│   │   └── FinancialApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── static/          # Web Dashboard UI (index.html, app.css, app.js)
│   └── pom.xml
├── run.sh
├── atlas_financial.db
└── README.md
```
