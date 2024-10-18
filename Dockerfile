FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} gestion-station-ski-1.0.jar

ENTRYPOINT ["java", "-jar", "/gestion-stations-ski-1.0.jar"]

EXPOSE 8081
