FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY product-catalog-service/pom.xml product-catalog-service/pom.xml
COPY customer-order-service/pom.xml customer-order-service/pom.xml
RUN mvn -pl product-catalog-service -am dependency:go-offline
COPY product-catalog-service product-catalog-service
RUN mvn -pl product-catalog-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/product-catalog-service/target/product-catalog-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
