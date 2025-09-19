FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
 
# ---- Stage 2: Create the final runtime image ----
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the JAR from the 'build' stage
COPY --from=build /app/target/after-market-db-tool.jar .
# Expose the necessary port (e.g., for a web server)
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "after-market-db-tool.jar"]