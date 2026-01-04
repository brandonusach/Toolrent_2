FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
EXPOSE 8080
COPY target/*.jar api-gateway.jar
ENTRYPOINT ["java","-jar","/api-gateway.jar"]

