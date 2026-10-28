import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val localProperties = Properties().apply {
    val file = file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val localSdk = providers.environmentVariable("SABERA_SDK_PATH").orNull
    ?: localProperties.getProperty("sabera.sdk.path")
if (localSdk != null) {
    val sdkApp = file(localSdk).resolve("app")
    require(sdkApp.resolve("glasses-sdk").isDirectory) { "SABERA_SDK_PATH must point to the jig-glass repository" }
    includeBuild(sdkApp) {
        name = "local-sabera-sdk"
        dependencySubstitution {
            substitute(module("jp.jig.sabera.app.sdk:sabera-app-core"))
                .using(project(":glasses-sdk:sabera-app-core"))
        }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://sdk.sabera.glass/maven")
    }
}

rootProject.name = "sabera-app-sdk-kmp-sample"
include(":app")
include(":shared")
include(":snippets")
