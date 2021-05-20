# build
FROM maven:3.8.1-adoptopenjdk-16 as build
ADD . /build/
WORKDIR /build/
RUN mvn clean install

# package
FROM openjdk:16.0-jdk
WORKDIR /app
COPY --from=build /build/target/subscriber-api.jar /app/
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar subscriber-api.jar