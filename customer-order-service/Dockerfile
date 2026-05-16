FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY product-catalog-service/pom.xml product-catalog-service/pom.xml
COPY customer-order-service/pom.xml customer-order-service/pom.xml
RUN mvn -pl customer-order-service -am dependency:go-offline
COPY customer-order-service customer-order-service
RUN mvn -pl customer-order-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/customer-order-service/target/customer-order-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
