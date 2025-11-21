# Étape 1 : Build avec Maven + Java 11
FROM maven:3.9.2-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution du WAR avec JRE 11
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT:-8082}

ENTRYPOINT ["sh", "-c", "java -Xmx512m -Xms256m \
  -XX:MaxMetaspaceSize=128m \
  -XX:+UseG1GC \
  -XX:ReservedCodeCacheSize=64m \
  -jar app.jar \
  --spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
  --spring.datasource.username=${DB_USER} \
  --spring.datasource.password=${DB_PASS} \
  --spring.jpa.database-platform=${HIBERNATE_DIALECT} \
  --server.port=${PORT} \
  --server.address=0.0.0.0"]
