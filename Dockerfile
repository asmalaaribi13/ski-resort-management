#Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

#Set the working directory in the container
WORKDIR /app

#Copy the JAR file into the container
COPY target/gestion-station-ski-1.0.jar app.jar

#Expose the port on which your Spring Boot application will run
EXPOSE 8080

#Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
