# Contributing to home-crew-config-server

One of fifteen repositories that make up HomeCrew. The rules below are
identical in all fifteen, and in the twelve Java services they are enforced by
blocking git hooks rather than discovered in CI.

## Before you start

Three things, once per clone.

**1. JDK 25.** These projects target Java 25. An older JDK fails at compile
with `release version 25 not supported`, which reads like the project is
broken rather than the toolchain.

    java -version
    # macOS: JAVA_HOME=$(/usr/libexec/java_home -v 25)

**2. gitleaks.** The pre-commit hook fails loudly without it rather than
skipping the scan, because an unverified scan is not a pass.

    brew install gitleaks

**3. The hooks.** `exec-maven-plugin` sets `core.hooksPath` at Maven's
`initialize` phase, so one build is enough:

    ./mvnw validate

Or do it by hand:

    git config core.hooksPath .githooks

A fresh clone is unprotected until one of those two has run. If a commit that
should have been rejected goes through, that is the first thing to check:

    git config core.hooksPath      # want: .githooks

## Branching

`dev` is the integration branch. `main` is the release branch. Everything else
is a feature branch off `dev`, named to a fixed shape:

```
dev                                    exempt
main                                   exempt
dev__YYYYmmDD__lower_snake_name        everything else
```

Lowercase only, single underscores inside the name, double underscores between
sections. The year must be last year, this year or next year - a cheap typo
catch.

```
dev__20260916__add_spotless_config     ok
feature/spotless                       rejected
dev__20260916__Add-Config              rejected
dev__19990916__old                     rejected (implausible year)
```

The `dev__` prefix is literal and applies even in the two repositories whose
default branch is `main`. There is no `main__` form. That reads oddly the
first time; it is the pattern, not a mistake.

## Commits

Conventional Commits, subject capped at 100 characters:

```
<type>[(scope)][!]: <description>
```

Types: `feat fix docs style refactor perf test build ci chore revert`. Merge,
revert, `fixup!` and `squash!` messages pass through untouched.

```
fix(docker): copy the jar by glob instead of a pinned filename
build: add formatting, lint and coverage gates with git hooks
ci: sync infrastructure config during deployment
```

Lowercase after the type, no trailing period, imperative mood. Say what the
commit does, not what you did.

## The local gate

Three hooks run, and all three block. They live in `.githooks/`, which
`core.hooksPath` points at.

**pre-commit** - branch name, then gitleaks on the staged diff, then Spotless
on the staged Java files. Formatting failures tell you to run
`./mvnw spotless:apply`; they do not reformat behind your back, because a hook
that rewrites your index while you are committing is worse than one that stops
you.

**commit-msg** - the Conventional Commits pattern and the 100-character
subject cap. `Merge`, `Revert`, `fixup!` and `squash!` pass through untouched.

**pre-push** - branch name again, then `./mvnw clean verify`. This is the
expensive one and it is the last line before CI. If it is slow, that is
because it is running the same thing CI will run; a green push is a green
build.

The gates themselves:

| Gate | Tool | Phase | Blocking |
|---|---|---|---|
| Formatting | Spotless + Eclipse JDT (Google Java Style) | `validate` | yes |
| Conventions | Checkstyle | `validate` | yes |
| Static analysis | SpotBugs | `verify` | yes |
| Coverage | JaCoCo | `verify` | yes, at `jacoco.min.coverage` |

Useful commands:

    ./mvnw spotless:apply      fix formatting
    ./mvnw validate            formatting and conventions only, fast
    ./mvnw clean verify        everything, same as CI and pre-push

To run the full gate across every HomeCrew repository at once, use
`_standards/verify.sh` - `--fast` for formatting and conventions only,
`--diff` to see what changed without running anything.

## Opening a pull request

Base the pull request on `dev`.

One logical change per pull request. A formatting sweep and a behaviour change
in the same diff means the reviewer reads neither carefully.

Fill in the template. The checklist is not ceremony - every line on it maps to
something that will otherwise be caught later and more expensively. Read your
own diff in the GitHub UI before you request a review; it is a different
reading experience from `git diff` and it catches different things.

## Where things live

Fifteen repositories means the right fix is often not in the repository where
you noticed the problem.

| Changing | Edit |
|---|---|
| A shared configuration value | `home-crew-config/application.yml` |
| Ports, images, compose topology, the deploy | `home-crew-infrastructure/docker-compose.yml` |
| A quality rule, a hook, checkstyle, spotbugs | `_standards/templates/`, then `./apply.py` |
| The CI secret-scan step | `_standards/add-ci-scan.py` |
| These community files | `_standards/templates/github/`, then `./add-github-meta.py` |
| Behaviour of this service | here |

Hardcoding a value here that belongs in home-crew-config works locally and
then diverges across twelve services. Adding a port here without mirroring it
into docker-compose works locally and then fails on deploy.

## Notes

- **Do not edit the generated files here.** `.githooks/`, `config/checkstyle.xml`,
  `config/checkstyle-suppressions.xml`, `config/spotbugs-exclude.xml`,
  `.editorconfig`, `lombok.config` and `.mvn/jvm.config` are copies. The next
  `apply.py` run overwrites them without asking. Change
  `_standards/templates/` instead.

- **The coverage floor starts at `0.00` and ratchets.** It was installed
  before the tests existed. Measure the real number with
  `./mvnw clean verify`, read `target/site/jacoco/index.html`, and raise
  `jacoco.min.coverage` to that baseline floored to the nearest 5%. Never
  lower it to make a build pass.

- **gitleaks is required, not optional.** The hook blocks the commit when the
  binary is missing rather than skipping the scan. If it finds something real:
  rotate the credential first, then remove it from history. Anything that
  reached a remote is compromised whether or not you deleted the commit.

- **Spotless must stay first in the pom.** Spotless and Checkstyle both bind
  to `validate`, and Maven runs same-phase plugins in declaration order. Do
  not reorder them.

- **These community files are generated too.** `README.md` is seeded once and
  then yours to edit. Everything else under `.github/` except `workflows/`
  comes from `_standards/templates/github/` and is overwritten on every
  `add-github-meta.py` run.

## Code of conduct

By participating you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md).
