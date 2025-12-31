# ---------- Build stage ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /build

# Copy pom first for dependency caching
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /build/target/grooviest-1.0-SNAPSHOT.jar app.jar

# Run the bot
CMD ["java", "-jar", "app.jar"]