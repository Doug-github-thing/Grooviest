# Use a lightweight Java 17 runtime image
FROM eclipse-temurin:17-jre-jammy

# Set working directory inside the container
WORKDIR /app

# Copy Lavalink files into the container
COPY Lavalink.jar .
COPY application.yml .
COPY plugins ./plugins

# Lavalink server uses port 2333
EXPOSE 2333

CMD ["java", "-jar", "Lavalink.jar"]
