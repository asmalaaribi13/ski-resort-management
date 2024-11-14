FROM openjdk:17-jre-slim
COPY target/*.jar gestion-station-ski-1.0.jar
ENTRYPOINT ["java", "-jar" ,"/gestion-station-ski-1.0.jar"]

EXPOSE 8080


