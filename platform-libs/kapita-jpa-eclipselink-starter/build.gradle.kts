plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.2"

dependencies {
    api(project(":kapita-domain-entity-core"))
    api(project(":kapita-jpa-eclipselink-autoconfigure"))
    api("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion") {
        exclude(group = "org.hibernate.orm", module = "hibernate-core")
    }
    api("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.2")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-jpa-eclipselink-starter"
        }
    }
}
