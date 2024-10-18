# 1. Utiliser l'image de base avec Java 17
FROM openjdk:17-jdk-alpine

# 2. Définir l'argument du fichier JAR
ARG JAR_FILE=target/*.jar

# 3. Créer un répertoire de travail
WORKDIR /app

# 4. Copier le fichier JAR dans le conteneur
COPY ${JAR_FILE} gestion-stations-ski-1.0.jar

# 5. Exposer le port de l'application
EXPOSE 8081

# 6. Commande de démarrage de l'application
ENTRYPOINT ["java", "-jar", "/app/gestion-stations-ski-1.0.jar"]
