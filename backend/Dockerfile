FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/web-server-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

