FROM openjdk:17-slim AS build
WORKDIR /home/gradle/src

COPY build.gradle.kts build.gradle.kts
COPY gradle.properties gradle.properties
COPY gradlew gradlew
COPY settings.gradle.kts settings.gradle.kts
COPY gradle/ gradle/
RUN ./gradlew build --no-daemon

COPY src/ src/
RUN ./gradlew build --no-daemon


FROM openjdk:17-slim
WORKDIR /app/data
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
