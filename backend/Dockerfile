# Step 1: Build stage using OpenJDK 21 & Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package application JAR without running tests
RUN mvn clean package -DskipTests

# Step 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled JAR artifact from build stage
COPY --from=build /app/target/financial-1.0.0.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Run Spring Boot Application
ENTRYPOINT ["java", "-jar", "app.jar"]
