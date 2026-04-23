FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY backend/demo/pom.xml backend/demo/pom.xml
COPY backend/demo/src backend/demo/src
COPY Frontend Frontend

WORKDIR /app/backend/demo
RUN mvn -q -DskipTests dependency:go-offline
RUN mvn -q clean package -DskipTests -DskipFrontendCopy=false

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/backend/demo/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

CMD ["java", "-jar", "/app/app.jar"]
