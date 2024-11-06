FROM openjdk:17-jdk

WORKDIR /app

COPY /home/vagrant/cheimaaa/target/gestion-station-ski-1.8.jar app.jar

EXPOSE 8089 

ENTRYPOINT ["java", "-jar", "app.jar"]

