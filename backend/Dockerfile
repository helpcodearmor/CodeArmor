# Stage 1: build with Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
# Copy only maven wrapper & pom first to cache dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn
# Copy source
COPY src ./src
# Use mvnw if present; otherwise use mvn from image
RUN if [ -f ./mvnw ]; then chmod +x ./mvnw && ./mvnw -DskipTests package; else mvn -DskipTests package; fi
# Stage 2: runtime using a stable Temurin JRE 21 image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy jar from builder stage (matches any jar name)
COPY --from=builder /app/target/*.jar app.jar
# Informational expose
EXPOSE 8080
# Default JVM options (can override via env var on Render)
ENV JAVA_OPTS=""
# Start the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
