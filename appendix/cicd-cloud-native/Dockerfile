# Multi-stage Dockerfile for CI/CD Cloud Native Application

# Stage 1: Build environment
FROM maven:3.8-openjdk-17-slim AS builder

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -g 1000 -S appgroup && \
    adduser -u 1000 -S appuser -G appgroup

# Install required packages
RUN apk add --no-cache \
    curl \
    jq \
    && rm -rf /var/cache/apk/*

WORKDIR /app

# Copy JAR file
COPY --from=builder /app/target/*.jar app.jar

# Copy entrypoint script
COPY docker/scripts/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]