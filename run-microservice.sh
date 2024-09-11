#!/bin/bash

mvn clean package -Dmaven.test.skip=true --no-transfer-progress

AGENT_FILE=opentelemetry-javaagent.jar
if [ ! -f "${AGENT_FILE}" ]; then
  curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar --output ${AGENT_FILE}
fi

export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOG_EXPORTER=none
export OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:5555

export OTEL_RESOURCE_ATTRIBUTES=service.name=service-layer,service.version=1.0
export OTEL_TRACES_SAMPLER=always_on
export OTEL_IMR_EXPORT_INTERVAL=1000
export OTEL_METRIC_EXPORT_INTERVAL=1000

java -javaagent:./${AGENT_FILE} -jar target/omsvclyr-core-0.0.1-SNAPSHOT.jar
