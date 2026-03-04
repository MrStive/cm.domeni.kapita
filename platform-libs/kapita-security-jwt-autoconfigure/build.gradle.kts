plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.13"

dependencies {
    api("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server:$springBootVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-security-jwt-autoconfigure"
        }
    }
}
