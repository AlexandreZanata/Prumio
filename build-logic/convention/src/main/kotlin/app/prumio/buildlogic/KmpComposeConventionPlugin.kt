package app.prumio.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * `prumio.kmp.compose`: a shared module that contains Compose UI and/or Compose resources.
 *
 * Generated resource accessors live in `<module package>.resources.Res` and are internal by default;
 * a module that must share strings sets `compose.resources.publicResClass = true` itself.
 */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("prumio.kmp.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<KotlinMultiplatformExtension> {
                androidLibraryTarget {
                    androidResources {
                        enable = true
                    }
                }
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.library("compose-components-resources"))
                    implementation(libs.library("compose-foundation"))
                    implementation(libs.library("compose-runtime"))
                    implementation(libs.library("compose-ui"))
                    implementation(libs.library("compose-ui-tooling-preview"))
                }
            }

            configureScreenshotTests()

            extensions.configure<ComposeExtension> {
                (this as ExtensionAware).extensions.configure<ResourcesExtension> {
                    packageOfResClass = "$prumioNamespace.resources"
                    publicResClass = false
                    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
                }
            }
        }
    }
}

/**
 * Screenshot tests (Roborazzi on Robolectric) verify against the committed images by default;
 * `./gradlew <task> -PrecordScreenshots` re-records them (docs/ai/recipes/add-design-system-component.md).
 */
private fun Project.configureScreenshotTests() {
    val record = providers.gradleProperty("recordScreenshots").isPresent
    tasks.withType<Test>().configureEach {
        systemProperty(if (record) "roborazzi.test.record" else "roborazzi.test.verify", "true")
        systemProperty("robolectric.graphicsMode", "NATIVE")
        maxHeapSize = "2g"
        // Robolectric reaches into JDK internals (FileDescriptor) on recent Android SDKs.
        jvmArgs(
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED",
        )
    }
}
