# syntax=docker/dockerfile:1

##########################  Build stage  ##########################
# Full JDK + Maven only here; none of it ships in the final image.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# 1) Resolve dependencies first, keyed only on pom.xml.
#    This layer is cached and re-used until pom.xml changes, so
#    day-to-day source edits skip the whole dependency download.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

# 2) Build the fat jar (tests/spotless/jacoco skipped for image builds).
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q clean package -DskipTests

# 3) Split the Spring Boot jar into layers (deps change rarely,
#    app code changes often) so Docker can cache them separately.
RUN java -Djarmode=layertools -jar target/*.jar extract --destination extracted

# 4) OpenTelemetry Java agent, pinned. Fetched here so the runtime
#    stage needs no network and no curl/wget install.
ARG OTEL_AGENT_VERSION=2.30.0
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /workspace/otel-agent.jar
RUN jar tf /workspace/otel-agent.jar > /dev/null

##########################  Runtime stage  #######################
# Slim Alpine JRE — no compiler, no Maven, ~lean base.
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Matches server.port in application.yml.
ARG SERVER_PORT=6069
ENV SERVER_PORT=${SERVER_PORT}

# Run as an unprivileged user, never root.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build --chown=spring:spring /workspace/otel-agent.jar /app/otel-agent.jar

# Kafka broker CA is inlined in application.yml (PEM truststore), so there is
# no keystore to COPY and no truststore password. Point the image at the managed
# broker; KAFKA_BROKER_URL / KAFKA_USERNAME / KAFKA_PASSWORD come in at run time.
ENV KAFKA_SECURITY_PROTOCOL=SASL_SSL

# Copy layers least-changed -> most-changed for maximum cache hits.
COPY --from=build --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/application/ ./

EXPOSE ${SERVER_PORT}

# Telemetry defaults. The endpoint and the auth header carry credentials,
# so they are NEVER baked in — supply them at run time:
#   -e OTEL_EXPORTER_OTLP_ENDPOINT=https://otlp-gateway-<region>.grafana.net/otlp
#   -e OTEL_EXPORTER_OTLP_HEADERS='Authorization=Basic <base64 instanceId:token>'
# Set OTEL_JAVAAGENT_ENABLED=false to run without instrumentation.
ENV OTEL_SERVICE_NAME=herald \
    OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf \
    OTEL_JAVAAGENT_ENABLED=true \
    OTEL_METRICS_EXPORTER=otlp \
    OTEL_LOGS_EXPORTER=otlp \
    OTEL_EXPORTER_OTLP_ENDPOINT=https://otlp-gateway-prod-ap-south-1.grafana.net/otlp \
    OTEL_EXPORTER_OTLP_HEADERS='Authorization=Basic MTYxNzk3OTpnbGNfZXlKdklqb2lNVGMxTVRjd01TSXNJbTRpT2lKemRHRmpheTB4TmpFM09UYzVMVzkwYkhBdGQzSnBkR1V0ZEdWemRDSXNJbXNpT2lKdU5qTTNRbFUyTVhJeGRuRldZbVo1T0VaQ01UaG9PRzhpTENKdElqcDdJbklpT2lKd2NtOWtMV0Z3TFhOdmRYUm9MVEVpZlgwPQ=='

# Container-aware heap sizing + fail fast on OOM. The JVM reads
# JAVA_TOOL_OPTIONS automatically, so java stays PID 1 and receives
# SIGTERM directly -> Spring graceful shutdown works.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 \
-XX:+ExitOnOutOfMemoryError \
-XX:MaxMetaspaceSize=192m \
-XX:ReservedCodeCacheSize=64m \
-javaagent:/app/otel-agent.jar"

# BusyBox wget ships with Alpine; hits the actuator health endpoint.
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -q -O - "http://localhost:${SERVER_PORT}/actuator/health" || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
