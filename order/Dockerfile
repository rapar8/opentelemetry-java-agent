FROM maven:3.9.3-eclipse-temurin-17-alpine as service-layer-build
WORKDIR /service-layer
COPY . /service-layer
RUN mvn clean package -DskipTests --no-transfer-progress && rm -rf /service-layer/.git
RUN ls target
RUN ls

FROM amazoncorretto:17 as O1-33

WORKDIR /service-layer
RUN apk --no-cache add curl
COPY --from=service-layer-build /service-layer/target/omsvclyr*.jar ./service-layer.jar
COPY otel-javaagent.1.33.jar ./otel-javaagent.jar
RUN pwd && ls

ENV OTEL_SERVICE_NAME=order
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","shipping.jar"]

FROM amazoncorretto:17 as O2-8

WORKDIR /shipping
#RUN apk --no-cache add curl
COPY --from=service-layer-build /service-layer/target/*.jar ./service-layer.jar
COPY otel-javaagent.2.8.jar ./otel-javaagent.jar
RUN pwd && ls

ENV OTEL_SERVICE_NAME=order
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","service-layer.jar"]