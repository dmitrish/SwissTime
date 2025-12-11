plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// Keep buildSrc minimal to satisfy external task invocations like :buildSrc:classes
// No dependencies are required unless you add Gradle plugins or helpers here.
