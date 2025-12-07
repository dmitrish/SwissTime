plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val agpVersion: String by project
val kotlinVersion: String by project

dependencies {
    // AGP must be on buildSrc runtime classpath for typed Android DSL in precompiled plugins
    implementation("com.android.tools.build:gradle:$agpVersion")
    // Kotlin plugin APIs are compileOnly to avoid conflicts with the main build's plugin management
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
