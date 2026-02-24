# Kapita Platform Libraries

Shared libraries for Kapita microservices:

- `kapita-platform-bom`
- `kapita-domain-entity-core`
- `kapita-security-jwt-autoconfigure`
- `kapita-security-jwt-starter`
- `kapita-jpa-eclipselink-autoconfigure`
- `kapita-jpa-eclipselink-starter`

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

### 2. Publish to Nexus (snapshot or release)

Set repository URLs and credentials:

```bash
export NEXUS_MAVEN_SNAPSHOTS_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_MAVEN_RELEASES_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_CREDENTIALS_USR="admin"
export NEXUS_CREDENTIALS_PSW="9d912f7d-c29a-4795-bd0a-b17481659304"
```

Publish a snapshot:

```bash
./gradlew -p ../platform-libs publish -PkapitaPlatformVersion=0.1.1-SNAPSHOT
```

Publish a release:

```bash
./gradlew -p ../platform-libs publish -PkapitaPlatformVersion=0.1.1
```

### 3. Publish all with helper script

From `platform-libs`:

```bash
export NEXUS_MAVEN_URL="http://localhost:8081/repository/kapita-releases/"
export NEXUS_CREDENTIALS_USR="admin"
export NEXUS_CREDENTIALS_PSW="your-password"
./scripts/publish-all.sh 0.1.1-SNAPSHOT
```

Notes:
- `NEXUS_MAVEN_URL` can be used as a shortcut (it fills both snapshot and release URLs).
- You can still set `NEXUS_MAVEN_SNAPSHOTS_URL` and `NEXUS_MAVEN_RELEASES_URL` separately.
- If version ends with `-SNAPSHOT`, script publishes to snapshot URL; otherwise to release URL.

Notes:
- For `*-SNAPSHOT`, Gradle publishes to `NEXUS_MAVEN_SNAPSHOTS_URL`.
- For non-snapshot versions, Gradle publishes to `NEXUS_MAVEN_RELEASES_URL`.
- You can also pass `-PnexusSnapshotsUrl=... -PnexusReleasesUrl=... -PnexusUsername=... -PnexusPassword=...`.

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
