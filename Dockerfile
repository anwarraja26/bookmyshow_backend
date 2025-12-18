# Multi-stage build for the Spring Boot backend
# Stage 1: build the application using Maven with a JDK
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -ntp -DskipTests package

# Stage 2: run the application with a lightweight JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy only the fat jar built in the previous stage
COPY --from=build /app/target/movie-booking-0.0.1-SNAPSHOT.jar app.jar
# Spring Boot default port
EXPOSE 8080
# Configure JVM for container friendliness and run the jar
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-XX:MinRAMPercentage=25.0","-jar","/app/app.jar"]
