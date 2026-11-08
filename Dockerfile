# Stage 1: Build the Spring Boot application
FROM maven:3.9.11-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy Maven configuration and download dependencies first (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Lightweight production runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Ensure upload directory exists for incident media attachments
RUN mkdir -p /app/uploads

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Railway injects the PORT environment variable dynamically (defaults to 8080)
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
