# Prebuilt jar required: run `./gradlew :blog-api:bootJar` before building this image.
FROM eclipse-temurin:21-jre-alpine

RUN addgroup --system contentria && adduser --system --ingroup contentria contentria
WORKDIR /app

ARG JAR_FILE=blog-api/build/libs/blog-api-*.jar
COPY ${JAR_FILE} app.jar

USER contentria
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
