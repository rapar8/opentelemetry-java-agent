FROM maven:3.9.3-eclipse-temurin-17-alpine as service-layer-build
WORKDIR /service-layer
COPY . /service-layer
RUN mvn clean package -DskipTests --no-transfer-progress && rm -rf /service-layer/.git


FROM eclipse-temurin:17.0.12_7-jre-alpine

WORKDIR /service-layer
RUN apk --no-cache add curl
RUN pwd
COPY opentelemetry-javaagent.jar ./otel-javaagent.jar
COPY maven-cache/repository /root/.m2/repository
COPY --from=service-layer-build /service-layer/target/omsvclyr*.jar ./service-layer.jar

ENV CLOUD_PROJECT_ID=omsvclyr-d
ENV K_REVISION=omsvclyr-k
ENV OTEL_JAVAAGENT_ENABLED=true
ENV OTEL_JAVAAGENT_DEBUG=true
ENV OTEL_INSTRUMENTATION_HTTP_CLIENT_EMIT_EXPERIMENTAL_TELEMETRY=true
ENV OTEL_INSTRUMENTATION_HTTP_SERVER_EMIT_EXPERIMENTAL_TELEMETRY=true
ENV OTEL_LOG_LEVEL=none

ENV OTEL_RESOURCE_ATTRIBUTES="service.name=omsvclyr,ing.system_name=omsvclyr,ing.team=ORDEXE,service.version=ORDEXE-V,deployment.environment=dev,ing.legal_company=ing,ing.version=0.0.1"
#export OTEL_EXPORTER_OTLP_PROTOCOL=grpc  # for gRPC protocol
ENV OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf


CMD ["java", "-javaagent:otel-javaagent.jar", "-jar","service-layer.jar"]