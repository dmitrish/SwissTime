plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.coroutines.baselineprofile"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    defaultConfig {
        minSdk = 28
        //noinspection EditedTargetSdkVersion
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments["androidx.benchmark.fullTracing.enable"] = "true"

        testInstrumentationRunnerArguments["androidx.benchmark.output.enable"] = "true"
    }

    targetProjectPath = ":app"

}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.tracing.perfetto)
    implementation(libs.androidx.tracing.perfetto.binary)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { apk ->
                val builtArtifacts = artifactsLoader.load(apk)
                requireNotNull(builtArtifacts) { "Built artifacts not found for ${v.name}" }
                builtArtifacts.applicationId
            }
        )
    }
}

// Mark connected Android test tasks in this module as not compatible with
// Gradle Configuration Cache. These tasks are known to be CC-incompatible
// and attempting to cache them causes serialization errors.
tasks.configureEach {
    if (name.contains("connected", ignoreCase = true) || name.endsWith("AndroidTest")) {
        notCompatibleWithConfigurationCache("Connected Android tests are not compatible with configuration cache in this module")
    }
}