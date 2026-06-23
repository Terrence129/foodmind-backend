# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom first for better Docker cache
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Required because the container is Linux-based
RUN chmod +x mvnw

# Download dependencies first
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build jar
RUN ./mvnw clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]