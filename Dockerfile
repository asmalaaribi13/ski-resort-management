FROM openjdk:17-jdk

EXPOSE 8081

WORKDIR /app

COPY target/gestion-station-ski-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]