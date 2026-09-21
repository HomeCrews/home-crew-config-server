#!/bin/sh
# Check: staged files are already formatted to the project style.
#
# This never edits your files. Spotless runs in :check mode and the hook tells
# you the command to fix it, so a commit only ever contains bytes you have seen.
set -eu
. "$(dirname "$0")/../common.sh"

# Only pay for a JVM start-up when something Spotless owns actually changed.
#
# This MUST track the <includes>/<excludes> in pom-plugins.xml. It used to match
# '\.java$' only, which meant a trailing space saved into a yml - or a missing
# final newline in .gitattributes - passed the hook reporting "no Java staged"
# and then failed the build on push. Spotless has never been Java-only.
staged=$(git diff --cached --name-only --diff-filter=ACMR \
    | grep -Ev '^mvnw$|^target/|^\.mvn/wrapper/' \
    | grep -Ev '\.cmd$' \
    | grep -E '\.(java|xml|yml|yaml|properties|sh|md)$|(^|/)Dockerfile$|^\.githooks/|^\.(gitattributes|gitignore|editorconfig)$' \
    || true)

if [ -z "$staged" ]; then
    ok "nothing Spotless owns staged; skipped formatting check"
    exit 0
fi

is_maven_repo || exit 0

MVNW=$(mvn_wrapper) || fail "no Maven wrapper (./mvnw) and no mvn on PATH."

info "checking formatting with Spotless..."

# Spotless prints the entire offending file on a violation, which buries the
# useful line. Capture the output and surface only the paths.
set +e
output=$("$MVNW" --batch-mode spotless:check 2>&1)
status=$?
set -e

if [ $status -ne 0 ]; then
    printf '\n  Unformatted files:\n' >&2
    printf '%s\n' "$output" \
        | grep -oE "[^ ]*\.(java|xml|yml|yaml|properties|sh|md)|[^ ]*(Dockerfile|\.gitattributes|\.gitignore|\.editorconfig)" \
        | sort -u \
        | sed 's|^|    |' >&2

    fail "Spotless found unformatted code." \
        "  Fix it with:" \
        "    ./mvnw spotless:apply" \
        "" \
        "  Then re-stage and commit again:" \
        "    git add -u && git commit"
fi

ok "formatting clean"
