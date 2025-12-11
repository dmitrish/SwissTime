// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.karumi:shot:6.0.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            ktfmt().googleStyle()
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
        }
        kotlinGradle {
            ktfmt().googleStyle()
        }
    }
    
    // Automatically check formatting on build
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn("spotlessCheck")
    }
}
