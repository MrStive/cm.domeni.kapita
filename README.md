# cm.domeni.kapita

## Dependency Strategy (Team Friendly)

This repository supports two modes for `platform-libs` dependencies:

1. Local source mode (default when `../platform-libs` exists):
`cm.domeni.kapita/settings.gradle.kts` auto-activates `includeBuild("../platform-libs")`.
No Nexus access is needed.

2. Remote Nexus mode:
Use published artifacts when local sources are unavailable or when you want CI/release parity.

## Java Version

- Project toolchains are set to Java 25.
- Gradle can auto-download a matching JDK via the Foojay toolchain resolver if Java 25 is not installed locally.

## Local Developer Setup

From `cm.domeni.kapita/cm.domeni.kapita`:

```bash
./gradlew compileJava
```

If `../platform-libs` is present, Gradle resolves libs from local source automatically.

## Publish Platform Libraries

From `cm.domeni.kapita` root:

```bash
export NEXUS_MAVEN_URL="http://localhost:8083/repository/kapita-releases"
export NEXUS_MAVEN_SNAPSHOTS_URL="http://localhost:8083/repository/kapita-releases"
export NEXUS_CREDENTIALS_USR="admin"
export NEXUS_CREDENTIALS_PSW="9d912f7d-c29a-4795-bd0a-b17481659304"
./cm.domeni.kapita/gradlew --no-daemon -p platform-libs publish -PkapitaPlatformVersion=0.1.1-SNAPSHOT
```

Or use the helper script:

```bash
cd platform-libs
./scripts/publish-all.sh 0.1.1-SNAPSHOT
```

For details about where to get these variables, see `platform-libs/README.md`.

## Force Remote Nexus

Use one of these options:

```bash
./gradlew -PforceRemotePlatformLibs=true compileJava
```

or

```bash
export FORCE_REMOTE_PLATFORM_LIBS=true
./gradlew compileJava
```

Configure Nexus endpoint and credentials via:

- `nexusMavenPublicUrl`, `nexusUsername`, `nexusPassword` in `cm.domeni.kapita/gradle.properties`, or
- `NEXUS_MAVEN_PUBLIC_URL`, `NEXUS_CREDENTIALS_USR`, `NEXUS_CREDENTIALS_PSW`.
