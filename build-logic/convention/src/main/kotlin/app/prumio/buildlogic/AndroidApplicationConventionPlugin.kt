package app.prumio.buildlogic

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * `prumio.android.application`: the Android entry point (:app:android).
 *
 * Also registers the privacy gate: `check` fails if the merged manifest of any variant requests
 * network access. Prumio is offline by design (PRIVACY.md, ADR-0009).
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val javaVersion = JavaVersion.toVersion(libs.version("jvm-target"))
            extensions.configure<ApplicationExtension> {
                namespace = prumioNamespace
                compileSdk = libs.version("android-compileSdk").toInt()
                defaultConfig {
                    minSdk = libs.version("android-minSdk").toInt()
                    targetSdk = libs.version("android-targetSdk").toInt()
                }
                compileOptions {
                    sourceCompatibility = javaVersion
                    targetCompatibility = javaVersion
                }
                buildFeatures {
                    compose = true
                }
            }

            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions.jvmTarget.set(JvmTarget.fromTarget(libs.version("jvm-target")))
            }

            extensions.configure<ApplicationAndroidComponentsExtension> {
                onVariants { variant ->
                    val taskName = "verify${variant.name.replaceFirstChar(Char::titlecase)}NoNetworkPermission"
                    val verify = tasks.register<VerifyNoNetworkPermissionTask>(taskName) {
                        mergedManifest.set(variant.artifacts.get(SingleArtifact.MERGED_MANIFEST))
                        report.set(layout.buildDirectory.file("reports/privacy/$taskName.txt"))
                    }
                    tasks.named("check").configure { dependsOn(verify) }
                }
            }

            enforceModuleRules()
        }
    }
}
