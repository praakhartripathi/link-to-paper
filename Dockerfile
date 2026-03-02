# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage Dockerfile for Link2Paper
# Uses jammy (Ubuntu 22.04) tags — support both AMD64 and ARM64 (Apple Silicon)
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: Build React frontend ────────────────────────────────────────────
FROM node:20-alpine AS frontend-build

WORKDIR /frontend

COPY frontend/package*.json ./
RUN npm ci --silent

COPY frontend/ .
RUN npm run build

# ── Stage 2: Build Spring Boot JAR ───────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS backend-build

WORKDIR /backend

# Download dependencies first (cache layer)
COPY backend/pom.xml .
COPY backend/mvnw .
COPY backend/.mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q || true

# Copy source and build
COPY backend/src ./src

# Copy React build output into Spring Boot static resources
COPY --from=frontend-build /frontend/dist ./src/main/resources/static

RUN ./mvnw clean package -DskipTests -q && \
    mv target/*.jar target/app.jar

# ── Stage 3: Lean runtime image ───────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

COPY --from=backend-build /backend/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dserver.port=${PORT:-8080}", \
  "-jar", "app.jar"]
