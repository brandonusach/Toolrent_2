FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ms-rates.jar
ENTRYPOINT ["java","-jar","/ms-rates.jar"]

