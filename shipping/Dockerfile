FROM maven:3.9.3-eclipse-temurin-17-alpine as shipping-build
WORKDIR /shipping
COPY . /shipping
RUN mvn clean package -DskipTests --no-transfer-progress && rm -rf /shipping/.git
RUN ls target
RUN ls

FROM amazoncorretto:17 as O1-33

WORKDIR /shipping
RUN apk --no-cache add curl
COPY --from=shipping-build /shipping/target/*.jar ./shipping.jar
COPY otel-javaagent.1.33.jar ./otel-javaagent.jar
RUN pwd && ls

ENV OTEL_SERVICE_NAME=shipping
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","shipping.jar"]

FROM amazoncorretto:17 as O2-8

WORKDIR /shipping
#RUN apk --no-cache add curl
COPY --from=shipping-build /shipping/target/*.jar ./shipping.jar
COPY otel-javaagent.2.8.jar ./otel-javaagent.jar
RUN pwd && ls

ENV OTEL_SERVICE_NAME=shipping
CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","shipping.jar"]