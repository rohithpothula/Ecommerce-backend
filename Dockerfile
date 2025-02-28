# Base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .
RUN chmod +x mvnw

# Copy project files
COPY src/ /home/app/src/
COPY pom.xml .

# Build project
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "target/ecommerce-backend-0.0.1-SNAPSHOT.jar"]
