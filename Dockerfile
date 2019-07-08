FROM openjdk:8-jre-alpine

EXPOSE 8080

COPY target/sokol-0.1.0-SNAPSHOT.war app.war

ENTRYPOINT ["java", "-jar", "app.war"]

#docker build -t sokol5 .