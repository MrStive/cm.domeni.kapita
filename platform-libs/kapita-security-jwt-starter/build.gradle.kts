plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.2"

dependencies {
    api(project(":kapita-security-jwt-autoconfigure"))
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server:$springBootVersion")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-security-jwt-starter"
        }
    }
}
