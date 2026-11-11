package app.prumio.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

/**
 * Executable version of the dependency rules in docs/architecture/modules.md.
 *
 * Every convention plugin calls [enforceModuleRules]. Adding a project dependency that is not
 * listed here fails the build at configuration time with an explanation. To change a rule,
 * update docs/architecture/modules.md and this table in the same change (and record why in an ADR).
 */
internal object ModuleRules {
    private const val TESTING_MODULE = ":core:testing"
    private const val DATABASE_TESTING_MODULE = ":core:database-testing"
    private const val SCREENSHOT_TESTING_MODULE = ":core:screenshot-testing"
    private val testOnlyModules = setOf(TESTING_MODULE, DATABASE_TESTING_MODULE, SCREENSHOT_TESTING_MODULE)

    private data class Rule(val module: String, val allowed: List<String>, val reason: String)

    private val rules = listOf(
        Rule(
            module = ":app:android",
            allowed = listOf(":app:shell", ":core:*"),
            reason = "the Android app is a thin entry point; screens are reached through :app:shell.",
        ),
        Rule(
            module = ":app:shell",
            allowed = listOf(":core:*", ":feature:*"),
            reason = "the shell assembles features and core modules into one app.",
        ),
        Rule(
            module = ":feature:*",
            allowed = listOf(
                ":core:common",
                ":core:designsystem",
                ":core:domain",
                ":core:model",
                ":core:navigation",
                ":core:ui",
            ),
            reason = "features talk to data only through :core:domain and never to other features.",
        ),
        Rule(
            module = ":core:ui",
            allowed = listOf(":core:common", ":core:designsystem", ":core:domain", ":core:model"),
            reason = "shared UI renders models and domain results (ADR-0019); it never loads data.",
        ),
        Rule(
            module = ":core:designsystem",
            allowed = listOf(":core:common"),
            reason = "the design system is generic and knows nothing about Prumio's domain.",
        ),
        Rule(
            module = ":core:data",
            allowed = listOf(":core:common", ":core:database", ":core:domain", ":core:model"),
            reason = "data implements domain ports on top of the database.",
        ),
        Rule(
            module = ":core:notifications",
            allowed = listOf(":core:common", ":core:domain", ":core:model"),
            reason = "notifications implement domain scheduling ports.",
        ),
        Rule(
            module = ":core:domain",
            allowed = listOf(":core:common", ":core:model"),
            reason = "the domain is pure Kotlin business logic.",
        ),
        Rule(
            module = ":core:database",
            allowed = listOf(":core:common"),
            reason = "the database module owns SQL only.",
        ),
        Rule(
            module = SCREENSHOT_TESTING_MODULE,
            allowed = listOf(":core:common", ":core:designsystem"),
            reason = "screenshot tests render components inside PrumioTheme.",
        ),
        Rule(
            module = DATABASE_TESTING_MODULE,
            allowed = listOf(":core:common", ":core:database"),
            reason = "the in-memory test database wraps the production schema.",
        ),
        Rule(
            module = TESTING_MODULE,
            allowed = listOf(":core:common", ":core:domain", ":core:model"),
            reason = "test doubles implement domain ports.",
        ),
        Rule(module = ":core:model", allowed = listOf(":core:common"), reason = "models are plain data."),
        Rule(module = ":core:navigation", allowed = emptyList(), reason = "navigation keys are leaf contracts."),
        Rule(module = ":core:common", allowed = emptyList(), reason = "common is the bottom of the graph."),
        Rule(module = ":tooling:*", allowed = emptyList(), reason = "tooling inspects sources, it does not link them."),
    )

    fun check(from: String, to: String, configurationName: String) {
        if (from == to) return
        if (to in testOnlyModules && configurationName.contains("test", ignoreCase = true)) return
        val rule = rules.firstOrNull { from.matchesPattern(it.module) }
            ?: throw GradleException(
                "Module '$from' has no dependency rule. Add it to build-logic/.../ModuleRules.kt " +
                    "and docs/architecture/modules.md.",
            )
        if (rule.allowed.none { to.matchesPattern(it) }) {
            throw GradleException(
                """
                |Module rule violation: '$from' must not depend on '$to' (configuration '$configurationName').
                |Allowed for '${rule.module}': ${rule.allowed.ifEmpty {
                    listOf("nothing")
                }.joinToString()}${testingNote()}.
                |Why: ${rule.reason}
                |See docs/architecture/modules.md#dependency-rules
                """.trimMargin(),
            )
        }
    }

    private fun testingNote() = " (plus ${testOnlyModules.joinToString()} in test source sets)"

    private fun String.matchesPattern(pattern: String): Boolean =
        if (pattern.endsWith(":*")) startsWith(pattern.dropLast(1)) else this == pattern
}

/** Fails the build as soon as a forbidden project dependency is declared. */
internal fun Project.enforceModuleRules() {
    val from = path
    configurations.configureEach {
        val configurationName = name
        dependencies.withType(ProjectDependency::class.java).configureEach {
            ModuleRules.check(from = from, to = this.path, configurationName = configurationName)
        }
    }
}
