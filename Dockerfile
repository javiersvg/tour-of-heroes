FROM store/oracle/serverjre:8

ADD https://github.com/javiersvg/tour-of-heroes/releases/download/0.0.1/tour-of-heroes-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]