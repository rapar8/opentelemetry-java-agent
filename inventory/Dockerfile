FROM maven:3.9.3-eclipse-temurin-17-alpine as inventory-build
WORKDIR /inventory
COPY . /inventory
RUN mvn clean package -DskipTests --no-transfer-progress && rm -rf /inventory/.git
RUN pwd && ls -a && ls target

FROM amazoncorretto:17 as O1-33

WORKDIR /inventory
RUN apk --no-cache add curl
COPY --from=inventory-build /inventory/target/*.jar ./inventory.jar
COPY otel-javaagent.1.33.jar ./otel-javaagent.jar
RUN pwd && ls
ENV OTEL_SERVICE_NAME=inventory
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","inventory.jar"]

FROM amazoncorretto:17 as O2-8

WORKDIR /shipping
#RUN apk --no-cache add curl
COPY --from=inventory-build /inventory/target/*.jar ./inventory.jar
COPY otel-javaagent.2.8.jar ./otel-javaagent.jar
RUN pwd && ls
ENV OTEL_SERVICE_NAME=inventory
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","inventory.jar"]