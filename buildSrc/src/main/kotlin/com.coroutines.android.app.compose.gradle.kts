import com.android.build.api.dsl.ApplicationExtension
import org.gradle.kotlin.dsl.configure

// Typed convention: configure Android application modules with Compose and Java targets
plugins.withId("com.android.application") {
    extensions.configure<ApplicationExtension> {
        configureKotlinAndroid(project)

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            "implementation"(platform(bom))
            "testImplementation"(platform(bom))
            "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
        }
    }
}
