package app.prumio.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * `prumio.feature`: a user-facing feature (screens + ViewModels).
 *
 * Wires every dependency a feature is allowed to have, so feature build files stay almost empty.
 * A feature never depends on another feature, :core:data, :core:database or :core:notifications.
 */
class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("prumio.kmp.compose")

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(project(":core:common"))
                    implementation(project(":core:designsystem"))
                    implementation(project(":core:domain"))
                    implementation(project(":core:model"))
                    implementation(project(":core:navigation"))
                    implementation(project(":core:ui"))

                    implementation(libs.library("androidx-lifecycle-runtime-compose"))
                    implementation(libs.library("androidx-lifecycle-viewmodel-compose"))
                    implementation(libs.library("androidx-navigation3-ui"))
                    implementation(libs.library("kotlinx-datetime"))

                    implementation(project.dependencies.platform(libs.library("koin-bom")))
                    implementation(libs.library("koin-compose"))
                    implementation(libs.library("koin-compose-viewmodel"))
                    implementation(libs.library("koin-core"))
                }
                sourceSets.getByName("commonTest").dependencies {
                    implementation(project(":core:testing"))
                }
            }
        }
    }
}
