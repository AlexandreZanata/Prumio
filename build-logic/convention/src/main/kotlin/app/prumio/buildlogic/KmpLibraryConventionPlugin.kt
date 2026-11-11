package app.prumio.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * `prumio.kmp.library`: base for every shared module.
 *
 * Android is the only target until iOS is explicitly requested; see docs/architecture/platform-strategy.md.
 * Code in commonMain must stay platform-neutral so iOS can be added without rewriting it.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")

            extensions.configure<KotlinMultiplatformExtension> {
                androidLibraryTarget {
                    namespace = prumioNamespace
                    compileSdk = libs.version("android-compileSdk").toInt()
                    minSdk = libs.version("android-minSdk").toInt()
                    withHostTest {}
                    compilerOptions {
                        jvmTarget.set(JvmTarget.fromTarget(libs.version("jvm-target")))
                    }
                }

                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }

                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.library("kotlinx-coroutines-core"))
                }
                sourceSets.getByName("commonTest").dependencies {
                    implementation(libs.library("kotlin-test"))
                    implementation(libs.library("kotlinx-coroutines-test"))
                    implementation(libs.library("turbine"))
                }
                sourceSets.getByName("androidHostTest").dependencies {
                    implementation(libs.library("kotlin-test-junit"))
                }
            }

            enforceModuleRules()
        }
    }
}
