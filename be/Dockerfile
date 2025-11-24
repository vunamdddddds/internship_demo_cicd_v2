# Stage 1: Build the application with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set the working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the builder stage
COPY --from=builder /app/target/InternShip-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
