FROM openjdk:11-jre-slim
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} gestion-station-ski-1.0.jar
ENTRYPOINT ["java", "-jar" ,"/gestion-station-ski-1.0.jar"]
EXPOSE 8089