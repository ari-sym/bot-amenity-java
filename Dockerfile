FROM adoptopenjdk/openjdk11:alpine-jre

RUN addgroup -S #template# && adduser -S #template# -G #template#
USER #template#:#template#

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]