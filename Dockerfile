FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/file-server-1.0.0.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]

