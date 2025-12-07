import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

// Typed convention: configure Android library modules with Compose and Java targets
plugins.withId("com.android.library") {
    extensions.configure<LibraryExtension> {
        configureKotlinAndroid(project)
    }
}
