package app.prumio.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** The `libs` version catalog (gradle/libs.versions.toml). */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.version(alias: String): String = findVersion(alias).get().requiredVersion

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> = findLibrary(alias).get()

/**
 * Kotlin package and Android namespace of a module, derived from its Gradle path.
 * `:core:domain` -> `app.prumio.core.domain`, `:tooling:architecture-tests` -> `app.prumio.tooling.architecturetests`.
 */
internal val Project.prumioNamespace: String
    get() = "app.prumio" + path.replace(':', '.').replace("-", "")

/** Configures the Android target of a Kotlin Multiplatform module (`kotlin { android { } }`). */
internal fun KotlinMultiplatformExtension.androidLibraryTarget(
    action: Action<KotlinMultiplatformAndroidLibraryTarget>,
) {
    (this as ExtensionAware).extensions.configure("android", action)
}
