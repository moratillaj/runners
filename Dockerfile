FROM openjdk:14-jdk-slim
COPY target/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
