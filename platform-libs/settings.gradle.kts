plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kapita-platform-libs"

include(
    "kapita-platform-bom",
    "kapita-domain-entity-core",
    "kapita-security-jwt-autoconfigure",
    "kapita-security-jwt-starter",
    "kapita-jpa-eclipselink-autoconfigure",
    "kapita-jpa-eclipselink-starter",
)
