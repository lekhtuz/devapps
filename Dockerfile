#FROM openjdk:17-alpine
FROM eclipse-temurin:21.0.1_12-jre-alpine
LABEL author=lekhtuz@gmail.com
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]