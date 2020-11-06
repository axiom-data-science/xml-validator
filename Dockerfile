# syntax=docker/dockerfile:experimental
# NOTE: DOCKER_BUILDKIT=1 also must be set
# DOCKER_BUILDKIT=1 docker build -t xml-validator .

FROM maven:3-jdk-8 as builder

WORKDIR /src

COPY . .

RUN --mount=type=cache,id=m2_repo,target=/root/.m2/repository \
    mvn clean package

FROM openjdk:8-jre-buster

WORKDIR /app

COPY --from=builder /src/target/xml-validator.jar /app/xml-validator.jar

CMD ["java", "-jar", "/app/xml-validator.jar", "/doc.xml"]
