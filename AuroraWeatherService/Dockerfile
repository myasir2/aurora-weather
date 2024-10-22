FROM --platform=linux/amd64 openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/AuroraWeatherService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar","/app.jar"]
