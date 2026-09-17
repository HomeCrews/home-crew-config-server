# home-crew-config-server

Spring Cloud Config Server. Serves home-crew-config to every service.

One of fifteen repositories that make up HomeCrew, a home-services marketplace
built as a Spring Boot microservice system. This one is a single service; it
is not useful on its own.

## At a glance

| | |
|---|---|
| Port | 8888 |
| Route through the gateway | not routed |
| Java package | `com.homecrew.configserver` |
| Maven artifact | `configserver` |
| Image | `mthanuj/homecrew-config-server:dev` |
| Compose service | `config-server` |
| Database | none |
| Default branch | `dev` |

Spring Boot 4.1.1 on Java 25, Spring Cloud 2025.1.3, built with Maven.

## Running it

The whole stack, which is the easy way:

    git clone git@github.com:HomeCrews/home-crew-infrastructure.git
    cd home-crew-infrastructure
    cp .env.example .env
    docker compose up -d

Just this service, against a stack that is already up. service-discovery
(8761) and config-server (8888) have to be running first, unless this is one
of them:

    ./mvnw spring-boot:run
    curl http://localhost:8888/actuator/health

Build and test:

    ./mvnw clean verify

## Configuration

`src/main/resources/application.properties` holds the port and the Eureka and
config-server URLs. The `docker` profile in `application-docker.properties`
swaps `localhost` for the compose hostnames; docker compose sets
`SPRING_PROFILES_ACTIVE=docker`.

Anything shared with other services belongs in
[home-crew-config](https://github.com/HomeCrews/home-crew-config), not here.
Hardcoding it works locally and then diverges across twelve services.

## How it is deployed

A push to `dev` builds the image and pushes two tags to Docker Hub:

    mthanuj/homecrew-config-server:dev
    mthanuj/homecrew-config-server:sha-<short>

CI then fires a `repository_dispatch` at home-crew-infrastructure, which pulls
the new image and restarts the container on the Hetzner dev host. There are no
releases and no version tags.

## Quality gates

The same blocking gates run in every HomeCrew repository. They run locally at
commit and push time rather than being discovered in CI.

| Gate | Tool | Runs at |
|---|---|---|
| Formatting | Spotless + google-java-format, AOSP, 4-space, 100-col | `validate` |
| Conventions | Checkstyle | `validate` |
| Static analysis | SpotBugs | `verify` |
| Coverage | JaCoCo, floor at `jacoco.min.coverage` | `verify` |
| Secrets | gitleaks | pre-commit, CI |
| Branch names | `.githooks/checks/branch-name.sh` | pre-commit, pre-push |
| Commit messages | Conventional Commits | commit-msg |

`.githooks/`, `config/checkstyle.xml`, `config/spotbugs-exclude.xml`,
`.editorconfig` and `lombok.config` are generated. Edit them in
`_standards/templates/` and re-run `apply.py`; a local edit is overwritten on
the next run. See `.github/CONTRIBUTING.md` for the workflow and
`_standards/README.md` for what each gate does and why.

## Contributing

See [CONTRIBUTING.md](.github/CONTRIBUTING.md). Branch names and commit
messages are checked by a blocking hook, so read it before your first commit.

## Related repositories

HomeCrew is fifteen repositories. The ones you are most likely to need next:

| Repository | What it is | Port |
|---|---|---|
| [home-crew-infrastructure](https://github.com/HomeCrews/home-crew-infrastructure) | docker compose topology and the Hetzner deploy | - |
| [home-crew-config](https://github.com/HomeCrews/home-crew-config) | shared configuration, served by config-server | - |
| [home-crew-service-discovery](https://github.com/HomeCrews/home-crew-service-discovery) | Eureka registry | 8761 |
| [home-crew-config-server](https://github.com/HomeCrews/home-crew-config-server) | Spring Cloud Config server | 8888 |
| [home-crew-api-gateway](https://github.com/HomeCrews/home-crew-api-gateway) | single entry point, routes to everything below | 8080 |
| [home-crew-user-service](https://github.com/HomeCrews/home-crew-user-service) | `/users/**` | 8081 |
| [home-crew-auth-service](https://github.com/HomeCrews/home-crew-auth-service) | `/auth/**` | 8082 |
| [home-crew-admin-service](https://github.com/HomeCrews/home-crew-admin-service) | `/admin/**` | 8083 |
| [home-crew-booking-service](https://github.com/HomeCrews/home-crew-booking-service) | `/bookings/**` | 8084 |
| [home-crew-worker-service](https://github.com/HomeCrews/home-crew-worker-service) | `/workers/**` | 8085 |
| [home-crew-notification-service](https://github.com/HomeCrews/home-crew-notification-service) | `/notifications/**` | 8086 |
| [home-crew-payment-service](https://github.com/HomeCrews/home-crew-payment-service) | `/payments/**` | 8087 |
| [home-crew-xp-service](https://github.com/HomeCrews/home-crew-xp-service) | `/xp/**` | 8088 |
| [home-crew-assignment-service](https://github.com/HomeCrews/home-crew-assignment-service) | `/assignments/**` | 8089 |
| [home-crew-webapp](https://github.com/HomeCrews/home-crew-webapp) | web frontend, not yet scaffolded | - |

## Licence

MIT. See [LICENSE](LICENSE).
