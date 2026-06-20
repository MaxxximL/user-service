FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests -pl user-service-impl -am

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/user-service-impl/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]