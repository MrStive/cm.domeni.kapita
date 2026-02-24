plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.2"
val springVersion = "6.2.2"
val springDataVersion = "3.4.2"

dependencies {
    api(project(":kapita-domain-entity-core"))
    api("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    api("org.springframework:spring-orm:$springVersion")
    api("org.springframework.data:spring-data-jpa:$springDataVersion")
    api("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc:$springBootVersion")
    testRuntimeOnly("com.h2database:h2:2.3.232")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-jpa-eclipselink-autoconfigure"
        }
    }
}
