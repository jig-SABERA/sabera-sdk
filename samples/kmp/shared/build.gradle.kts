plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "SampleShared"
            isStatic = true
            binaryOption("bundleId", "jp.jig.sabera.sample.shared")
            export("jp.jig.sabera.app.sdk:sabera-app-core:1.0.0")
        }
    }
    sourceSets {
        commonMain.dependencies {
            api("jp.jig.sabera.app.sdk:sabera-app-core:1.0.0")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }
        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.10.0")
        }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}

android {
    namespace = "jp.jig.glasses.sample.shared"
    compileSdk = 37
    defaultConfig { minSdk = 31 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
