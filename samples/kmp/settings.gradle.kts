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
        // ローカル検証用。publishToMavenLocal した SDK を先に見る
        mavenLocal()
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
include(":snippets")
