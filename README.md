# Link2Paper — AI Technical Paper Generator

> Paste any webpage URL → get a downloadable technical research paper PDF, powered by AI.

## Project Overview

Link2Paper is a full-stack web application that transforms any web page into a downloadable, AI-generated technical research paper in PDF format.

For detailed information on the project architecture, tech stack, and setup instructions, please see the [full documentation](documentation.txt).

## Quick Start

### 1. Backend

```bash
cd backend
# Add your OpenAI API key in src/main/resources/application.properties
mvn spring-boot:run
```

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

The application will be running at `http://localhost:5173`.

## Docker

Alternatively, you can run the application using Docker Compose:

```bash
docker-compose up --build
```
