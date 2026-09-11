pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 公開パッケージでも GitHub Packages の Maven は認証を求めるため、資格情報が要る。
        // 名前から決まる GitHubPackagesUsername / GitHubPackagesPassword を Gradle が探す。
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages")
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "sabera-app-sdk-kmp-sample"
include(":app")
include(":snippets")
