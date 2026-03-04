import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.testing.Test
import java.net.URI

val kapitaPlatformVersion =
    providers.gradleProperty("kapitaPlatformVersion")
        .orElse(providers.environmentVariable("KAPITA_PLATFORM_VERSION"))
        .orElse("0.1.0-SNAPSHOT")
        .get()

allprojects {
    group = "com.domeni.kapita"
    version = kapitaPlatformVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            toolchain {
                languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(25))
            }
        }
        tasks.withType(Test::class.java).configureEach {
            useJUnitPlatform()
        }
    }

    plugins.withId("maven-publish") {
        val nexusReleasesUrl =
            providers.gradleProperty("nexusReleasesUrl")
                .orElse(providers.environmentVariable("NEXUS_MAVEN_RELEASES_URL"))
        val nexusSnapshotsUrl =
            providers.gradleProperty("nexusSnapshotsUrl")
                .orElse(providers.environmentVariable("NEXUS_MAVEN_SNAPSHOTS_URL"))
        val nexusUsername =
            providers.gradleProperty("nexusUsername")
                .orElse(providers.environmentVariable("NEXUS_CREDENTIALS_USR"))
        val nexusPassword =
            providers.gradleProperty("nexusPassword")
                .orElse(providers.environmentVariable("NEXUS_CREDENTIALS_PSW"))
        val isSnapshotVersion = version.toString().endsWith("SNAPSHOT")
        val targetRepositoryUrl =
            if (isSnapshotVersion) {
                nexusSnapshotsUrl.orNull
            } else {
                nexusReleasesUrl.orNull
            }
        val remotePublishRequested =
            gradle.startParameter.taskNames.any { taskName ->
                val normalized = taskName.substringAfterLast(':')
                normalized.contains("publish", ignoreCase = true) &&
                    !normalized.contains("mavenlocal", ignoreCase = true)
            }

        if (remotePublishRequested) {
            val missingConfiguration = mutableListOf<String>()
            if (targetRepositoryUrl.isNullOrBlank()) {
                missingConfiguration +=
                    if (isSnapshotVersion) {
                        "nexusSnapshotsUrl or NEXUS_MAVEN_SNAPSHOTS_URL"
                    } else {
                        "nexusReleasesUrl or NEXUS_MAVEN_RELEASES_URL"
                    }
            }
            if (nexusUsername.orNull.isNullOrBlank()) {
                missingConfiguration += "nexusUsername or NEXUS_CREDENTIALS_USR"
            }
            if (nexusPassword.orNull.isNullOrBlank()) {
                missingConfiguration += "nexusPassword or NEXUS_CREDENTIALS_PSW"
            }
            if (missingConfiguration.isNotEmpty()) {
                throw GradleException(
                    "Remote publish requested for ${project.path} (${version}) but missing: " +
                        missingConfiguration.joinToString(", "))
            }
        }

        extensions.configure(PublishingExtension::class.java) {
            repositories {
                if (!targetRepositoryUrl.isNullOrBlank()) {
                    maven {
                        name = "nexus"
                        url = URI(targetRepositoryUrl)
                        isAllowInsecureProtocol = targetRepositoryUrl.startsWith("http://")
                        credentials {
                            username = nexusUsername.orNull ?: ""
                            password = nexusPassword.orNull ?: ""
                        }
                    }
                }
            }
        }
    }
}
