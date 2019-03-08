FROM gradle:4.10-jdk8 as builder
LABEL maintainer="https://github.com/hmcts/ccd-data-store-api"

COPY . /home/gradle/src
USER root
RUN chown -R gradle:gradle /home/gradle/src
USER gradle

WORKDIR /home/gradle/src
RUN gradle assemble

FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-1.0

ENV JAVA_OPTS "-Djava.security.egd=file:/dev/./urandom -javaagent:/opt/app/applicationinsights-agent-2.3.1.jar"

COPY --from=builder /home/gradle/src/build/libs/core-case-data.jar /opt/app/
COPY lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

WORKDIR /opt/app

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" curl --silent --fail http://localhost:4452/status/health

EXPOSE 4452

CMD ["core-case-data.jar"]
