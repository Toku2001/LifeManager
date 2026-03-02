FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
	&& apt-get install -y --no-install-recommends sqlite3 \
	&& rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app/data/db /app/initdb

COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]