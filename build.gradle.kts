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
    // Use unversioned IDs for Android plugins to avoid conflicts with AGP on buildSrc classpath
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("com.android.test") apply false

    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.baselineprofile) apply false
}
