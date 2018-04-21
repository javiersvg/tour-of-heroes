FROM store/oracle/serverjre:8
VOLUME /tmp
ARG CACHE_TAG
RUN echo ${CACHE_TAG}
ADD JAR_FILE=https://github.com/javiersvg/tour-of-heroes/releases/download/${CACHE_TAG}/tour-of-heroes-${CACHE_TAG}.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]