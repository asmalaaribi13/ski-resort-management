FROM openjdk:17-jdk-alpine

COPY target/gestion-station-ski.jar gestion-station-ski.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/gestion-station-ski.jar"]

