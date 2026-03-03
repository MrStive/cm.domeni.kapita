#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PLATFORM_LIBS_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
DEFAULT_GRADLEW="${PLATFORM_LIBS_DIR}/../cm.domeni.kapita/gradlew"
VERSION="${1:-${KAPITA_PLATFORM_VERSION:-0.1.0-SNAPSHOT}}"

if [[ -n "${NEXUS_MAVEN_URL:-}" ]]; then
  export NEXUS_MAVEN_SNAPSHOTS_URL="${NEXUS_MAVEN_SNAPSHOTS_URL:-${NEXUS_MAVEN_URL}}"
  export NEXUS_MAVEN_RELEASES_URL="${NEXUS_MAVEN_RELEASES_URL:-${NEXUS_MAVEN_URL}}"
fi

: "${NEXUS_MAVEN_SNAPSHOTS_URL:?Missing NEXUS_MAVEN_SNAPSHOTS_URL (or NEXUS_MAVEN_URL)}"
: "${NEXUS_MAVEN_RELEASES_URL:?Missing NEXUS_MAVEN_RELEASES_URL (or NEXUS_MAVEN_URL)}"
: "${NEXUS_CREDENTIALS_USR:?Missing NEXUS_CREDENTIALS_USR}"
: "${NEXUS_CREDENTIALS_PSW:?Missing NEXUS_CREDENTIALS_PSW}"
export NEXUS_MAVEN_SNAPSHOTS_URL NEXUS_MAVEN_RELEASES_URL NEXUS_CREDENTIALS_USR NEXUS_CREDENTIALS_PSW

if [[ "${VERSION}" == *-SNAPSHOT ]]; then
  TARGET_REPOSITORY_URL="${NEXUS_MAVEN_SNAPSHOTS_URL}"
else
  TARGET_REPOSITORY_URL="${NEXUS_MAVEN_RELEASES_URL}"
fi

echo "Publishing Kapita platform libs version: ${VERSION}"
echo "Target repository: ${TARGET_REPOSITORY_URL}"

if command -v curl >/dev/null 2>&1; then
  http_code="$(
    curl -sS -o /dev/null -w '%{http_code}' \
      -u "${NEXUS_CREDENTIALS_USR}:${NEXUS_CREDENTIALS_PSW}" \
      "${TARGET_REPOSITORY_URL}"
  )"
  if [[ ! "${http_code}" =~ ^2[0-9][0-9]$ ]]; then
    echo "Nexus repository check failed (${http_code}): ${TARGET_REPOSITORY_URL}" >&2
    exit 1
  fi
fi

if [[ -x "${DEFAULT_GRADLEW}" ]]; then
  GRADLE_CMD=("${DEFAULT_GRADLEW}")
elif command -v gradle >/dev/null 2>&1; then
  GRADLE_CMD=("gradle")
else
  echo "No Gradle executable found. Expected ${DEFAULT_GRADLEW} or gradle in PATH." >&2
  exit 1
fi

"${GRADLE_CMD[@]}" --no-daemon -p "${PLATFORM_LIBS_DIR}" publish -PkapitaPlatformVersion="${VERSION}"

if command -v curl >/dev/null 2>&1; then
  artifacts=(
    "kapita-platform-bom"
    "kapita-domain-entity-core"
    "kapita-security-jwt-autoconfigure"
    "kapita-security-jwt-starter"
    "kapita-jpa-eclipselink-autoconfigure"
    "kapita-jpa-eclipselink-starter"
  )
  base_url="${TARGET_REPOSITORY_URL%/}/com/domeni/kapita"
  echo "Nexus metadata check (${VERSION}):"
  for artifact in "${artifacts[@]}"; do
    metadata_url="${base_url}/${artifact}/${VERSION}/maven-metadata.xml"
    status_code="$(curl -sS -o /dev/null -w '%{http_code}' "${metadata_url}" || true)"
    echo " - ${artifact}: ${status_code}"
  done
fi

echo "Publish completed."
