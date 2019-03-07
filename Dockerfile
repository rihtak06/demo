FROM 931604932544.dkr.ecr.us-east-2.amazonaws.com/technology-images:maven-3.5.4-jdk-8-alpine
LABEL maintainer="steerwise.io"
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    SLEEP=0 \
    JAVA_OPTS=" -XX:+UseParallelGC -Xms1024M -Xmx2048M "
CMD mvn clean install -DskipTests
COPY ./target/*.jar /app.jar

CMD echo "The application will start in ${SLEEP}s..." && \
    sleep ${SLEEP} && \
    java ${JAVA_OPTS}  -Dserver.port=8085  -jar /app.jar

EXPOSE 8085

