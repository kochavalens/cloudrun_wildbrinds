FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package -DskipTests

FROM openjdk:12.0.2
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/ms-consume-api-0.0.1-SNAPSHOT.jar /app
COPY --from=MAVEN_BUILD /build/lib/ /app/
ENTRYPOINT ["java", "-cp", "/app/ms-consume-api-0.0.1-SNAPSHOT.jar", "-Dloader.path=/app", "org.springframework.boot.loader.PropertiesLauncher"]