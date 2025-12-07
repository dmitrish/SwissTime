import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Shared convention for Android modules: enable Compose and set Java 11 compatibility.
 * Works for both Application and Library Android Gradle Plugin extensions.
 *
 * Also supports optionally reading compileSdk/minSdk from the version catalog (libs.versions.toml)
 * if versions named "projectCompileSdkVersion" and "projectMinSdkVersion" are defined.
 */
fun CommonExtension<*, *, *, *, *, *>.configureKotlinAndroid(project: Project) {
    // Try to read values from the "libs" version catalog, but only apply them if present.
    runCatching {
        with(project) {
            val compileSdkFromCatalog = libs.findVersion("projectCompileSdkVersion")
                .map { it.requiredVersion }
                .orElse(null)
                ?.toIntOrNull()
            val minSdkFromCatalog = libs.findVersion("projectMinSdkVersion")
                .map { it.requiredVersion }
                .orElse(null)
                ?.toIntOrNull()

            if (compileSdkFromCatalog != null) {
                compileSdk = compileSdkFromCatalog
            }
            if (minSdkFromCatalog != null) {
                defaultConfig.minSdk = minSdkFromCatalog
            }
        }

    }.onFailure {
        // Swallow errors to avoid breaking builds if catalog isn't available in this context
    }

    buildFeatures.apply {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
