FROM gradle:8.11.1-jdk21 AS build

COPY --chown=gradle:gradle . /home/gradle/thesis-management
WORKDIR /home/gradle/thesis-management/server

RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21

EXPOSE 8080

RUN mkdir /app && chmod 755 /app

COPY --from=build /home/gradle/thesis-management/server/build/libs/server-*.jar /app/server.jar
RUN chmod 755 /app/server.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/server.jar"]
