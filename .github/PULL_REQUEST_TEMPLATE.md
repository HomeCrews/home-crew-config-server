<!-- Delete any section that does not apply. An empty heading is noise. -->

## What changed

<!-- One paragraph. The diff is below; do not narrate it. Say what this makes
     possible, or what it stops happening. -->

## Why

<!-- Closes #123. If there is no issue, say what broke or what was missing. -->

## How to verify

    ./mvnw clean verify

If the change is user-visible, show what you hit and what came back:

    ./mvnw spring-boot:run
    curl http://localhost:8888/actuator/health

## Checklist

- [ ] Base branch is `dev`
- [ ] Branch name is `dev__YYYYmmDD__lower_snake_name`
- [ ] Every commit subject is Conventional Commits, 100 characters or fewer
- [ ] `./mvnw clean verify` passes locally
- [ ] Spotless and google-java-format clean (`validate`)
- [ ] Checkstyle clean (`validate`)
- [ ] SpotBugs clean (`verify`)
- [ ] JaCoCo at or above `jacoco.min.coverage` (`verify`)
- [ ] No secrets added; the pre-commit gitleaks scan is clean
- [ ] Shared configuration changed in home-crew-config, not hardcoded here
- [ ] Port, image or dependency changes mirrored in
      home-crew-infrastructure/docker-compose.yml
- [ ] README updated if behaviour, ports or setup changed

## Risk

<!-- Blast radius. Which services break if this is wrong? Does a config or
     compose change have to land first? Say "none, additive" if that is true -
     it is a useful thing for a reviewer to read. -->
