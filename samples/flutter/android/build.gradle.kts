allprojects {
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

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
