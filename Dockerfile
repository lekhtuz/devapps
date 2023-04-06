#FROM openjdk:17-alpine
FROM eclipse-temurin:17-jdk-alpine
LABEL author=lekhtuz@gmail.com
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]