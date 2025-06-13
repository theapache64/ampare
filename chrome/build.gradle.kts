// Add compose gradle plugin
plugins {
    kotlin("multiplatform") version "2.1.20"
}
group = "io.github.theapache64.ampare"
version = "1.0.0-alpha01"

// Add maven repositories
repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
            }
        }
    }
}
// Workaround for https://youtrack.jetbrains.com/issue/KT-49124
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}
