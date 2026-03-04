FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven config first (better caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render provides PORT env var; Spring reads it via server.port=${PORT:8080}
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]