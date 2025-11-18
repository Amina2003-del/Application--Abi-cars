# Étape 1 : Build avec Maven + Java 11
FROM maven:3.9.2-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution du WAR avec JRE 11
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 8082

# Lire les variables d'environnement Render pour Spring Boot
ENTRYPOINT ["sh", "-c", "java -jar app.war \
  --spring.datasource.url=${SPRING_DATASOURCE_URL} \
  --spring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
  --spring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
  --server.port=8082"]

