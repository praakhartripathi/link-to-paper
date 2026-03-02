# Link2Paper — AI Technical Paper Generator

> Paste any webpage URL → get a downloadable technical research paper PDF, powered by AI.

## Project Overview

A full-stack web application implementing the pipeline:

```
User URL → Jsoup Scraper → Content Cleaner → OpenAI LLM → iText PDF → Download
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18 + Vite + Tailwind CSS 3 |
| Backend | Java 17 + Spring Boot 3 |
| Scraping | Jsoup 1.17 |
| AI | OpenAI GPT-4o-mini via REST |
| PDF | iText 7 |

---

## Project Structure

```
web-to-pdf/
├── backend/                  # Spring Boot Maven project
│   ├── pom.xml
│   └── src/main/java/com/webtopdf/
│       ├── WebToPdfApplication.java
│       ├── config/CorsConfig.java
│       ├── controller/PaperController.java
│       ├── dto/PaperRequest.java
│       ├── dto/PaperResponse.java
│       └── service/
│           ├── WebScraperService.java
│           ├── ContentCleanerService.java
│           ├── AIService.java
│           └── PdfGeneratorService.java
├── frontend/                 # Vite React project
│   ├── index.html
│   ├── tailwind.config.js
│   └── src/
│       ├── App.jsx
│       ├── index.css
│       ├── services/api.js
│       └── components/
│           ├── Hero.jsx
│           ├── UrlInput.jsx
│           ├── LoadingState.jsx
│           ├── PaperPreview.jsx
│           └── DownloadButton.jsx
└── todo.txt
```

---

## Running Locally

### 1. Backend

```bash
cd backend

# Add your OpenAI API key:
# Edit src/main/resources/application.properties
# openai.api-key=sk-...your-key...

mvn spring-boot:run
# Starts on http://localhost:8080
```

> **No API key?** The backend automatically returns a **mock PDF** with placeholder content — great for demos and portfolio screenshots.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
# Opens http://localhost:5173
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/generate-paper` | Submit URL, receive PDF binary |
| `GET`  | `/api/health` | Health check |

**Request body:**
```json
{ "url": "https://en.wikipedia.org/wiki/Machine_learning" }
```

**Response:** `application/pdf` binary stream (direct download)

---

## Output Paper Structure

1. **Title**
2. **Abstract**
3. **Introduction**
4. **Methodology**
5. **Discussion / Analysis**
6. **Conclusion**
7. **References**

---

## Feature Roadmap

- **Phase 1 (MVP)** ✅ — Paste URL, generate paper, download PDF
- **Phase 2** — Citation styles (APA, IEEE), edit before download, history dashboard
- **Phase 3** — Multi-link synthesis, literature review generator, AI citation finder, plagiarism check

---

## Resume Value

Demonstrates: AI integration · Web scraping · Spring Boot REST APIs · Document generation · React SPA architecture · Real-world SaaS design
