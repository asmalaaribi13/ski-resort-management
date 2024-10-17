FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} gestion-station-ski.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/gestion-station-ski.jar"]

