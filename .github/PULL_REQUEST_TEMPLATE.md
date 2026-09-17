<!-- Base branch should be `dev`.

     What changed and why belongs in the linked issue, not here. This
     template is the evidence that the change is ready to merge, not a
     description of it.

     If it is not ready, convert it to a draft - "Convert to draft" in the
     Reviewers section of the Conversation tab. -->

Closes #

## AppConfig changes

<!-- Config changes, or "No application property changes".

     Three places a value can live, and picking the wrong one is the usual
     mistake:

       src/main/resources/application.properties   this service only
       application-docker.properties               this service, compose only
       home-crew-config                            shared; merges first
       docker-compose.yml + .env.example           environment, in
                                                   home-crew-infrastructure

     A value that belongs in home-crew-config and gets hardcoded here works
     locally and then diverges across twelve services. -->

## Local build output

The full gate, with nothing skipped:

    $ ./mvnw --batch-mode clean verify | tee build.log

    [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
    [INFO] BugInstance size is 0
    [INFO] All coverage checks have been met.
    [INFO] BUILD SUCCESS

Errors, if any:

    $ egrep '\[ERROR\] Failed to execute goal|\[ERROR\] .*\.java|\[ERROR\] com\.homecrew\.|\[ERROR\] Tests run:|Rule violated for bundle' build.log

<!-- Paste the real summary. No `-DskipTests`, `-Dspotless.check.skip`,
     `-Dcheckstyle.skip`, `-Dspotbugs.skip` or `-Djacoco.skip` - the point of
     pasting it is that it is the same gate CI and the pre-push hook run.

     If anything is amber rather than red - a flaky test, a suppressed
     SpotBugs finding, coverage that dropped, a warning you are living with -
     say so here and say what you decided. A known failure you have reasoned
     about is reviewable; a silent one is not. -->

## Before review

- [ ] Self review: you read your own diff in the GitHub UI and checked it
      against the Java and Spring conventions before requesting a review
- [ ] Tests: unit and integration tests written for the new code, not just
      passing for the old
- [ ] Coverage: JaCoCo did not go down. If it went up, `jacoco.min.coverage`
      was ratcheted to the new baseline floored to the nearest 5%
- [ ] Build output above is from this branch, with no skip flags

<!-- Re-request review from anyone who left comments once you have addressed
     them - they are not notified otherwise. -->

## Impact

<!-- Tick only what applies. Nothing the hooks already enforce is listed
     here: formatting, Checkstyle, SpotBugs, coverage, secrets, branch name
     and commit format are all green or this branch could not have been
     pushed. -->

- [ ] New or changed environment variable - also added to `.env.example` and
      to `docker-compose.yml` in home-crew-infrastructure
- [ ] Database schema, migration or seed script changed - also added to
      `postgres/init/` in home-crew-infrastructure
- [ ] Port, image name or healthcheck changed - also updated in
      `docker-compose.yml` and in the deploy allow-list in
      `.github/workflows/deploy.yml`
- [ ] New or changed shared configuration - lives in home-crew-config, and
      that has to merge first
- [ ] New endpoint - gateway route added in home-crew-api-gateway
- [ ] Breaking change to an endpoint the gateway or another service calls
- [ ] New dependency in `pom.xml`
- [ ] README updated - behaviour, ports or setup changed
- [ ] None of the above; this is self-contained

## Before merge

- [ ] Branch is up to date with `dev` and the full gate was re-run
      after the rebase or merge

<!-- Nothing to do after this merges. A merge to `dev` builds and publishes

         mthanuj/homecrew-config-server:dev

     then dispatches to home-crew-infrastructure, which pulls and restarts
     the container on the Hetzner dev host automatically.

     A merge to `main` publishes the `:latest` tag and deploys nowhere -
     there is no production path yet. -->
