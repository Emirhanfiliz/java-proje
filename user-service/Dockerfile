FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-library/pom.xml shared-library/
COPY user-service/pom.xml user-service/
COPY workout-service/pom.xml workout-service/
COPY statistics-service/pom.xml statistics-service/
COPY api-gateway/pom.xml api-gateway/
COPY desktop-client/pom.xml desktop-client/
COPY mobile-client/pom.xml mobile-client/

RUN mvn dependency:go-offline -pl user-service -am -B -q

COPY shared-library/src shared-library/src
COPY user-service/src user-service/src

RUN mvn clean package -pl user-service -am -DskipTests -B -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/user-service/target/user-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
