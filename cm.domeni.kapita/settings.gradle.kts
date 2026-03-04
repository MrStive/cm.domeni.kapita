plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kapita"

val platformLibsDir = file("../platform-libs")

val explicitUseLocalPlatformLibs =
    startParameter.projectProperties["useLocalPlatformLibs"]?.toBooleanStrictOrNull()
        ?: System.getenv("USE_LOCAL_PLATFORM_LIBS")?.toBooleanStrictOrNull()

val forceRemotePlatformLibs =
    startParameter.projectProperties["forceRemotePlatformLibs"]?.toBooleanStrictOrNull()
        ?: System.getenv("FORCE_REMOTE_PLATFORM_LIBS")?.toBooleanStrictOrNull()
        ?: false

val useLocalPlatformLibs =
    explicitUseLocalPlatformLibs
        ?: (!forceRemotePlatformLibs && platformLibsDir.exists())

if (useLocalPlatformLibs) {
    if (platformLibsDir.exists()) {
        includeBuild(platformLibsDir)
    } else {
        throw GradleException(
            "Local platform-libs requested but not found at ${platformLibsDir.absolutePath}",
        )
    }
}
