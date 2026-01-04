FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
EXPOSE 8888
COPY target/*.jar config-server.jar
ENTRYPOINT ["java","-jar","/config-server.jar"]

