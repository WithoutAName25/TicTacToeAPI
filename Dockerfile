FROM openjdk:17-slim AS build
WORKDIR /home/gradle/src

COPY client/build.gradle.kts client/
COPY common/build.gradle.kts common/
COPY server/build.gradle.kts server/
COPY gradle/ gradle/
COPY build.gradle.kts gradle.properties gradlew settings.gradle.kts ./
RUN ./gradlew build --no-daemon

COPY common/src/ common/src/
COPY server/src/ server/src/
RUN ./gradlew :server:build --no-daemon


FROM openjdk:17-slim
WORKDIR /app/data
COPY --from=build /home/gradle/src/server/build/libs/*-all.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
