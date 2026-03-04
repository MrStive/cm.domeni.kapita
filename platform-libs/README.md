# Kapita Platform Libraries

Shared libraries for Kapita microservices:

- `kapita-platform-bom`
- `kapita-domain-entity-core`
- `kapita-security-jwt-autoconfigure`
- `kapita-security-jwt-starter`
- `kapita-jpa-eclipselink-autoconfigure`
- `kapita-jpa-eclipselink-starter`

## Java Version

- Toolchains are set to Java 25.
- Gradle can auto-download a matching JDK via the Foojay toolchain resolver if Java 25 is not installed locally.

## Local Build

From the microservice root (which already contains `gradlew`):

```bash
./gradlew -p ../platform-libs build
```

## Publishing

### 1. Publish in local Maven cache

```bash
./gradlew -p ../platform-libs publishToMavenLocal
```

### 2. Publish to Nexus with one command (recommended)

From `platform-libs`:

```bash
export NEXUS_MAVEN_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_CREDENTIALS_USR="<your-nexus-user>"
export NEXUS_CREDENTIALS_PSW="<your-nexus-password-or-token>"
./scripts/publish-all.sh 0.1.1-SNAPSHOT
```

### 3. Publish to Nexus with Gradle (snapshot or release)

Set repository URLs and credentials:

```bash
export NEXUS_MAVEN_SNAPSHOTS_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_MAVEN_RELEASES_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_CREDENTIALS_USR="<your-nexus-user>"
export NEXUS_CREDENTIALS_PSW="<your-nexus-password-or-token>"
```

Publish a snapshot:

```bash
./gradlew -p ../platform-libs publish -PkapitaPlatformVersion=0.1.1-SNAPSHOT
```

Publish a release:

```bash
./gradlew -p ../platform-libs publish -PkapitaPlatformVersion=0.1.1
```

Notes:
- `NEXUS_MAVEN_URL` can be used as a shortcut (it fills both snapshot and release URLs).
- You can still set `NEXUS_MAVEN_SNAPSHOTS_URL` and `NEXUS_MAVEN_RELEASES_URL` separately.
- If version ends with `-SNAPSHOT`, script publishes to snapshot URL; otherwise to release URL.

Notes:
- For `*-SNAPSHOT`, Gradle publishes to `NEXUS_MAVEN_SNAPSHOTS_URL`.
- For non-snapshot versions, Gradle publishes to `NEXUS_MAVEN_RELEASES_URL`.
- You can also pass `-PnexusSnapshotsUrl=... -PnexusReleasesUrl=... -PnexusUsername=... -PnexusPassword=...`.

## Where to get environment variables

- `NEXUS_MAVEN_URL` (or `NEXUS_MAVEN_SNAPSHOTS_URL` / `NEXUS_MAVEN_RELEASES_URL`):
  get it from your Nexus repository URL, provided by your DevOps team or Nexus admin.
- `NEXUS_CREDENTIALS_USR` and `NEXUS_CREDENTIALS_PSW`:
  use your Nexus account credentials or an access token generated in Nexus.
- If you do not have values yet:
  request them from the team owning Nexus access (DevOps/platform).

Example: keep them in a local file and load them for the session:

```bash
mkdir -p ~/.kapita
cat > ~/.kapita/nexus.env <<'EOF'
export NEXUS_MAVEN_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_CREDENTIALS_USR="<your-nexus-user>"
export NEXUS_CREDENTIALS_PSW="<your-nexus-password-or-token>"
EOF
chmod 600 ~/.kapita/nexus.env
source ~/.kapita/nexus.env
```

Quick connectivity check:

```bash
curl -u "${NEXUS_CREDENTIALS_USR}:${NEXUS_CREDENTIALS_PSW}" "${NEXUS_MAVEN_URL}"
```

## Typical Usage In a Microservice

1. Import `kapita-platform-bom`.
2. Add `kapita-security-jwt-starter`.
3. Add `kapita-jpa-eclipselink-starter`.
4. Add `@EnableKapitaJpaRepositories(basePackages = "...")` in app config.

Example annotation:

```java
@EnableKapitaJpaRepositories(basePackages = "com.mycompany.myservice.repositories")
```

Example properties:

```yaml
kapita:
  security:
    jwt:
      issuer: "https://issuer.local"
      audience: "my-api"
      public-key-location: "classpath:security/jwt-public.pem"

spring:
  jpa:
    show-sql: false
    properties:
      eclipselink.weaving: "false"
      eclipselink.ddl-generation: "none"
      eclipselink.target-database: "PostgreSQL"
```

Repository and dependencies example:

```kotlin
repositories {
  mavenCentral()
  maven {
    url = uri("http://localhost:8081/repository/kapita-releases/")
    credentials {
      username = System.getenv("NEXUS_CREDENTIALS_USR")
      password = System.getenv("NEXUS_CREDENTIALS_PSW")
    }
  }
}

dependencies {
  implementation(platform("com.domeni.kapita:kapita-platform-bom:0.1.1-SNAPSHOT"))
  implementation("com.domeni.kapita:kapita-security-jwt-starter")
  implementation("com.domeni.kapita:kapita-jpa-eclipselink-starter")
}
```
