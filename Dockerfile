# Utiliser une image de base JDK 17 slim
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré dans l'image Docker
COPY target/gestion-station-ski-1.0.jar app.jar

# Spécifier le port d'écoute
EXPOSE 8081

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
