FROM openjdk:17.0.2-jdk-slim-buster
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]