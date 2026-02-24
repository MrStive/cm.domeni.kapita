plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.2")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-domain-entity-core"
        }
    }
}
