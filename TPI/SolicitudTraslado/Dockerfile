FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . /app
RUN mvn -B -DskipTests -Dproject.build.sourceEncoding=UTF-8 package


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/solicitudes.db
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.sqlite.JDBC
EXPOSE 8082
VOLUME ["/data"]
ENTRYPOINT ["java","-jar","/app/app.jar"]

# para keycloak (ajustar según tu configuración)
ENV KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080
ENV KEYCLOAK_REALM=backend-containers
ENV KEYCLOAK_CLIENT_ID=tpi-backend-client
ENV KEYCLOAK_CLIENT_SECRET=bfmtgdiEIuEwTufDxX1oFQz7BDdN30pP
ENV SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/backend-containers
ENV SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://keycloak:8080/realms/backend-containers/protocol/openid-connect/certs