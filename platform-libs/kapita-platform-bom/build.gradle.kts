plugins {
    `java-platform`
    `maven-publish`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":kapita-domain-entity-core"))
        api(project(":kapita-kafka-inbound-starter"))
        api("com.domeni.kapita:kapita-kafka-outbox-starter:${project.version}")
        api(project(":kapita-security-jwt-starter"))
        api(project(":kapita-jpa-eclipselink-starter"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
            artifactId = "kapita-platform-bom"
        }
    }
}
