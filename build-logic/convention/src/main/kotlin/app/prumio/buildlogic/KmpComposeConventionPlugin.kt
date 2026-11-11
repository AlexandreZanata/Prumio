package app.prumio.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
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
