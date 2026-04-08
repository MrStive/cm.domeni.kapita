plugins {
    `java-library`
    `maven-publish`
}

val springBootVersion = "3.4.13"
val springDataVersion = "3.4.13"
val springKafkaVersion = "3.3.11"
val jacksonVersion = "2.18.3"

dependencies {
    api("jakarta.persistence:jakarta.persistence-api:3.1.0")
    api("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    api("org.springframework.data:spring-data-jpa:$springDataVersion")
    api("org.springframework.kafka:spring-kafka:$springKafkaVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("org.jspecify:jspecify:1.0.0")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:5.22.0")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "kapita-kafka-inbound-autoconfigure"
        }
    }
}
