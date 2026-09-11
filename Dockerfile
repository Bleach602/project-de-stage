# ============================================================
# ÉTAPE 1 : BUILD (Compilation du projet Spring Boot)
# ============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copier le fichier de configuration des dépendances
COPY pom.xml .

# 2. Télécharger les dépendances (utilisant le cache BuildKit /root/.m2)
RUN --mount=type=cache,target=/root/.m2 mvn dependency:resolve -B

# 3. Copier le code source
COPY src ./src

# 4. Compiler l'application et générer le JAR sans exécuter les tests
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -B

# ============================================================
# ÉTAPE 2 : RUNTIME (Image finale légère pour l'exécution)
# ============================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie du JAR compilé depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Dossier pour le stockage éventuel des fichiers uploadés
RUN mkdir -p /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]