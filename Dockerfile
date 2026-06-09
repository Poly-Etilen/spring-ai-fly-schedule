FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/spring-ai-fly-schedule-practice-0.0.1-SNAPSHOT.jar app.jar

RUN chmod +x app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]