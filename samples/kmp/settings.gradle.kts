pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "glasses-sdk-kmp-sample"
include(":app")
