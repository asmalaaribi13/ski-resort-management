# 1. Utiliser l'image de base avec Java 17
FROM openjdk:17-jdk-alpine

# 2. Définir le répertoire de travail
WORKDIR /app

# 3. Copier le fichier JAR dans le conteneur
COPY target/gestion-stations-ski-1.0.jar /app/gestion-stations-ski-1.0.jar

# 4. Exposer le port de l'application
EXPOSE 8081

# 5. Commande de démarrage de l'application
ENTRYPOINT ["java", "-jar", "/app/gestion-stations-ski-1.0.jar"]
