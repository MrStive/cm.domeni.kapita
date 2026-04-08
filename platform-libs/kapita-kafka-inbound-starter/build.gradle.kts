plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.13"
val springKafkaVersion = "3.3.11"

dependencies {
    api(project(":kapita-kafka-inbound-autoconfigure"))
    api("org.springframework.boot:spring-boot-starter-json:$springBootVersion")
    api("org.springframework.kafka:spring-kafka:$springKafkaVersion")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-kafka-inbound-starter"
        }
    }
}
