plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("app.cash.paparazzi") version "2.0.0-alpha02"
}

apply(plugin = "shot")

android {
    namespace = "com.coroutines.swisstime"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.coroutines.clockwithtimezone"
        minSdk = 26  // Temporarily increased from 24 to 26 to resolve Scala and JSON4s library issues
        //noinspection EditedTargetSdkVersion
        targetSdk = 36
        versionCode = 10
        versionName = "1.43"

        testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
           // signingConfig = signingConfigs.getByName("debug")
            // signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":worldclockcommon"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)


    // Glance for App Widgets
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    // DataStore for preferences
    implementation(libs.androidx.datastore.preferences)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Play Core for in-app updates and reviews
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    //implementation ("com.google.android.play:core:1.7.2")

    implementation(libs.androidx.runtime.tracing)

    testImplementation(libs.junit)
    // MockK for mocking in tests
    testImplementation("io.mockk:mockk:1.13.8")
    // Play Core testing library for FakeAppUpdateManager
       //. androidTestImplementation("com.google.android.play:app-update-testing:2.1.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.shot)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation(libs.material)
    androidTestImplementation(libs.androidx.material3)
    androidTestImplementation("androidx.compose.material:material:1.6.0")
    androidTestImplementation("androidx.drawerlayout:drawerlayout:1.0.0")



}
