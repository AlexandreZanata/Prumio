package app.prumio.tooling.architecturetests

import kotlin.test.Test

/** Import-level layer rules (docs/architecture/modules.md#dependency-rules). */
class LayerDependencyTest {
    @Test
    fun `features never import data, database, notifications, the app or other features`() {
        val violations = Repository.appKotlinFiles
            .filter { it.module.startsWith("feature/") }
            .flatMap { file ->
                val ownPackage = "app.prumio." + file.module.replace('/', '.')
                file.imports
                    .filter { import ->
                        import.startsWith("app.prumio.core.data.") ||
                            import.startsWith("app.prumio.core.database.") ||
                            import.startsWith("app.prumio.core.notifications.") ||
                            import.startsWith("app.prumio.app.") ||
                            (import.startsWith("app.prumio.feature.") && !import.startsWith("$ownPackage."))
                    }
                    .map { "${file.path} imports $it" }
            }
        assertNoViolations("features depend only on core domain/model/ui/designsystem/navigation/common", violations)
    }

    @Test
    fun `only the design system touches Material directly`() {
        val violations = Repository.appKotlinFiles
            .filterNot { it.module == "core/designsystem" }
            .flatMap { file ->
                file.imports
                    .filter {
                        it.startsWith("androidx.compose.material3.") ||
                            it.startsWith("androidx.compose.material.")
                    }
                    .map { "${file.path} imports $it (use a Prumio* component; add one if missing)" }
            }
        assertNoViolations("Material components are wrapped by :core:designsystem (ADR-0008)", violations)
    }

    @Test
    fun `common, model and domain stay free of UI, storage and platform frameworks`() {
        val pureModules = setOf("core/common", "core/model", "core/domain")
        val forbidden =
            listOf("android.", "androidx.", "org.jetbrains.compose.", "app.cash.sqldelight.", "java.", "javax.")
        val violations = Repository.appProductionFiles
            .filter { it.module in pureModules }
            .flatMap { file ->
                file.imports.filter { import ->
                    forbidden.any { import.startsWith(it) }
                }.map { "${file.path} imports $it" }
            }
        assertNoViolations("pure Kotlin layers (ADR-0001)", violations)
    }

    @Test
    fun `commonMain code is platform neutral so iOS can be added later`() {
        val forbidden = listOf(
            "android.",
            "java.",
            "javax.",
            "androidx.activity.",
            "androidx.core.",
            "androidx.glance.",
            "androidx.sqlite.",
            "androidx.work.",
            "app.cash.sqldelight.driver.",
            "org.koin.android.",
        )
        val violations = Repository.appKotlinFiles
            .filter { it.sourceSet == "commonMain" || it.sourceSet == "commonTest" }
            .flatMap { file ->
                file.imports.filter { import -> forbidden.any { import.startsWith(it) } }
                    .map { "${file.path} imports $it (move it to androidMain behind an expect/interface)" }
            }
        assertNoViolations("commonMain is platform neutral (docs/architecture/platform-strategy.md)", violations)
    }

    @Test
    fun `Koin is referenced only from DI files, Routes and the Android entry point`() {
        val violations = Repository.appProductionFiles
            .filter { file -> file.imports.any { it.startsWith("org.koin.") } }
            .filterNot { file ->
                file.packageName.endsWith(".di") ||
                    file.path.endsWith("FeatureModule.kt") ||
                    file.path.endsWith("Route.kt") ||
                    file.module == "app/android"
            }
            .map { it.path }
        assertNoViolations(
            "business code is DI-framework agnostic (docs/architecture/dependency-injection.md)",
            violations,
        )
    }
}
