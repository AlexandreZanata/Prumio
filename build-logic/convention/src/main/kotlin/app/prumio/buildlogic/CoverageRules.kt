package app.prumio.buildlogic

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Executable version of docs/architecture/testing-strategy.md#coverage.
 *
 * Every shared module is instrumented by Kover; modules listed in [minimumLineCoverage] fail
 * `check` (task `koverVerify`) below their threshold. Raising a threshold is welcome; lowering one
 * needs the owner's approval and an update of the doc above.
 */
internal object CoverageRules {
    /** Minimum line coverage, in percent, per module path. */
    val minimumLineCoverage: Map<String, Int> = mapOf(
        ":core:domain" to 90,
        ":core:data" to 80,
    )

    /** Code that is wiring or generated, not logic: never counted. */
    val excludedClasses: List<String> = listOf(
        "app.prumio.*.di.*",
        "app.prumio.*.resources.*",
        "*ComposableSingletons*",
    )
}

internal fun Project.configureCoverage() {
    pluginManager.apply("org.jetbrains.kotlinx.kover")
    val isFeature = path.startsWith(":feature:")
    val minimum = CoverageRules.minimumLineCoverage[path] ?: if (isFeature) 80 else null
    extensions.configure<KoverProjectExtension> {
        reports {
            filters {
                if (isFeature) {
                    includes { classes("*ViewModel", "*ViewModel$*") }
                }
                excludes {
                    classes(CoverageRules.excludedClasses)
                }
            }
            if (minimum != null) {
                verify {
                    rule("Minimum line coverage of $path (build-logic CoverageRules.kt)") {
                        minBound(minimum)
                    }
                }
            }
        }
    }
}
