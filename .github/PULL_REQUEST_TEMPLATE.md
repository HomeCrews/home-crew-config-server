<!-- Base branch should be `dev`. Delete any section that does not
     apply - an empty heading is noise. -->

## What changed

<!-- One paragraph. The diff is below; do not narrate it. Say what this makes
     possible, or what it stops happening. -->

## Why

<!-- Closes #123. If there is no issue, say what broke or what was missing. -->

## Build output

<!-- The tail of the gate, with the real numbers. This is not proof that it
     ran - the pre-push hook would have blocked you otherwise - it is so the
     reviewer can see coverage and test counts without checking out. -->

Built locally on this branch with unit and integration tests enabled:

    $ ./mvnw clean install -fn | tee build.log

    [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
    [INFO] BugInstance size is 0
    [INFO] All coverage checks have been met.
    [INFO] BUILD SUCCESS

<!-- Paste the module build summary and the real numbers. `-fn`
     (--fail-never) means Maven runs every module and reports at the end
     instead of stopping at the first failure, so BUILD SUCCESS on the last
     line is not on its own proof that nothing failed - check the summary and
     the Failures section below. -->

## Failures and warnings

Pulled out of `build.log` rather than scrolled for:

    $ egrep '\[ERROR\] com.homecrew|Failed to run task|\[ERROR\] db.migration script error on' build.log

<!-- Paste the output. "None" is a real answer and a useful one.

     If anything is red, amber or skipped, say what you decided about it: a
     flaky test, a suppressed SpotBugs finding, coverage that went down, a
     warning you are living with. A known failure you have reasoned about is
     reviewable; a silent one is not. -->

## Impact

<!-- Tick only what applies. Nothing the hooks already enforce is listed here:
     formatting, Checkstyle, SpotBugs, coverage, secrets, branch name and
     commit format are all green or this branch could not have been pushed. -->

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

## Risk

<!-- Blast radius. Which services break if this is wrong, what has to land
     first, and how you would roll it back. "None, additive" is a useful
     thing for a reviewer to read. -->
