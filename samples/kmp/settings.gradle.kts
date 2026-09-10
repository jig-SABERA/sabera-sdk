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
        // 認証情報は名前から決まる GitHubPackagesUsername / GitHubPackagesPassword を
        // Gradle が探す。設定キャッシュには保存されず、必要になるまで要求もされない。
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages")
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "sabera-app-sdk-kmp-sample"
include(":app")
include(":shared")
include(":snippets")
