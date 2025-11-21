# Stage 1: build with Maven (uses the official Maven image that bundles JDK 17)
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app

# Copy only Maven files first to take advantage of layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
# If you don't have mvnw, remove mvnw references and use mvn instead

# Copy the rest of the source
COPY src ./src

# Build jar (skip tests to speed up build; remove -DskipTests if you need tests)
RUN ./mvnw -DskipTests package

# Stage 2: runtime using Temurin (reliable up-to-date JDK distribution)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# Copy the built jar produced by the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose (optional - informative; Render ignores EXPOSE)
EXPOSE 8080

# Use small default JVM options; override with env var if needed
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
