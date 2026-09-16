FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Glob, not a pinned filename: the artifact name embeds <version>, so a
# version bump in the pom would otherwise break the image build.
COPY target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
