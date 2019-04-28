FROM openjdk:11

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src

RUN ./gradlew build

FROM openjdk:11-jre
VOLUME /tmp
ARG JAR_FILE=build/libs/*SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
RUN ls *.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]