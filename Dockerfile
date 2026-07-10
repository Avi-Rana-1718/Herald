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

##########################  Runtime stage  #######################
# Slim Alpine JRE — no compiler, no Maven, ~lean base.
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Run as an unprivileged user, never root.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy layers least-changed -> most-changed for maximum cache hits.
COPY --from=build --chown=spring:spring /workspace/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /workspace/extracted/application/ ./

EXPOSE 9500

# Container-aware heap sizing + fail fast on OOM. The JVM reads
# JAVA_TOOL_OPTIONS automatically, so java stays PID 1 and receives
# SIGTERM directly -> Spring graceful shutdown works.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# BusyBox wget ships with Alpine; hits the actuator health endpoint.
HEALTHCHECK --interval=30s --timeout=3s --start-period=45s --retries=3 \
    CMD wget -q -O - http://localhost:9500/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
