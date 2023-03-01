FROM adoptopenjdk/openjdk11:alpine-jre

RUN addgroup -S amenity-java && adduser -S amenity-java -G amenity-java
USER amenity-java:amenity-java

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]