// ドキュメントに載せるコード例の置き場。コンパイルと ktlint を通すことが目的で、
// 成果物は使わない。scripts/sync-snippets.py がここから docs/ へコードを写す。
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "jp.jig.sabera.app.docs.snippets"
    compileSdk = 36

    defaultConfig {
        minSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xskip-prerelease-check")
        }
    }
}

dependencies {
    implementation("jp.jig.sabera.app.sdk:sabera-app-core:0.0.14")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}
