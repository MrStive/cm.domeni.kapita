# cm.domeni.kapita

## Dependency Strategy (Team Friendly)

This repository supports two modes for `platform-libs` dependencies:

1. Local source mode (default when `../platform-libs` exists):
`cm.domeni.kapita/settings.gradle.kts` auto-activates `includeBuild("../platform-libs")`.
No Nexus access is needed.

2. Remote Nexus mode:
Use published artifacts when local sources are unavailable or when you want CI/release parity.

## Local Developer Setup

From `cm.domeni.kapita/cm.domeni.kapita`:

```bash
./gradlew compileJava
```

If `../platform-libs` is present, Gradle resolves libs from local source automatically.

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
