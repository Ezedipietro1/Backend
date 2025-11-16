FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . /app
RUN mvn -B -DskipTests -Dproject.build.sourceEncoding=UTF-8 package


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# Configuración de Keycloak
ENV KEYCLOAK_ISSUER_URI=http://keycloak:8080/realms/TPI-Realm
ENV KEYCLOAK_JWK_SET_URI=http://keycloak:8080/realms/TPI-Realm/protocol/openid-connect/certs

# Configuración de servicios
ENV CLIENTES_SERVICE_URL=http://clientes:8081
ENV SOLICITUDES_SERVICE_URL=http://solicitudtraslado:8082

EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]