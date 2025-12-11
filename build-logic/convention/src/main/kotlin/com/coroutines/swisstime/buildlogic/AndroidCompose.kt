package com.coroutines.swisstime.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.gradle.api.artifacts.VersionCatalogsExtension

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    val libs = the<VersionCatalogsExtension>().named("libs")
    dependencies {
        add("implementation", platform(libs.findLibrary("androidx.compose.bom").get()))
        add("androidTestImplementation", platform(libs.findLibrary("androidx.compose.bom").get()))
        add("debugImplementation", libs.findLibrary("androidx.ui.tooling").get())
    }
}