package app.prumio.tooling.architecturetests

import kotlin.test.Test

/** Every module is documented, registered and wired (docs/ai/recipes/add-feature-module.md). */
class ModuleStructureTest {
    private val shellDi = "app/shell/src/commonMain/kotlin/app/prumio/app/shell/di/PrumioModules.kt"
    private val shellNavigation = "app/shell/src/commonMain/kotlin/app/prumio/app/shell/navigation/PrumioNavDisplay.kt"
    private val navigationKeys = "core/navigation/src/commonMain/kotlin/app/prumio/core/navigation/PrumioNavKeys.kt"
    private val navigationSerialization =
        "core/navigation/src/commonMain/kotlin/app/prumio/core/navigation/NavKeySerialization.kt"

    @Test
    fun `every module has a README contract and a CLAUDE pointer`() {
        val violations = Repository.modules.flatMap { module ->
            val missing = listOf("README.md", "CLAUDE.md").filterNot { Repository.exists("$module/$it") }
            missing.map { "$module/$it is missing" }
        }
        assertNoViolations("module contracts", violations)
    }

    @Test
    fun `every module is listed in the module catalog`() {
        val catalog = Repository.read("docs/architecture/modules.md")
        val violations = Repository.modules
            .map { ":" + it.replace('/', ':') }
            .filterNot { catalog.contains("`$it`") }
            .map { "$it is missing from docs/architecture/modules.md" }
        assertNoViolations("module catalog", violations)
    }

    @Test
    fun `every feature is wired into the app shell`() {
        val diFile = Repository.read(shellDi)
        val navFile = Repository.read(shellNavigation)
        val violations = Repository.modules.filter { it.startsWith("feature/") }.flatMap { module ->
            val name = module.substringAfter("feature/")
            val entriesDeclaration = "fun EntryProviderScope<NavKey>.${name}Entries("
            val providesEntries = Repository.appProductionFiles
                .any { it.module == module && it.text.contains(entriesDeclaration) }
            buildList {
                if ("${name}FeatureModule" !in diFile) add("$module: ${name}FeatureModule missing in PrumioModules.kt")
                if (providesEntries &&
                    "${name}Entries(" !in navFile
                ) {
                    add("$module: ${name}Entries missing in PrumioNavDisplay.kt")
                }
            }
        }
        assertNoViolations("features are reachable", violations)
    }

    @Test
    fun `every shared module is part of the merged coverage report`() {
        val rootBuild = Repository.read("build.gradle.kts")
        val violations = Repository.modules
            .filterNot { it == "app/android" || it.startsWith("tooling/") }
            .map { module ->
                module to
                    "kover(projects." + module.split('/').joinToString(".") { it.toCamelCase() } + ")"
            }
            .filterNot { (_, declaration) -> declaration in rootBuild }
            .map { (module, declaration) -> "$module: add `$declaration` to the root build.gradle.kts" }
        assertNoViolations(
            "coverage covers every shared module (docs/architecture/testing-strategy.md#coverage)",
            violations,
        )
    }

    private fun String.toCamelCase() = split('-').mapIndexed { index, part ->
        if (index == 0) part else part.replaceFirstChar(Char::uppercase)
    }.joinToString("")

    @Test
    fun `every navigation key is registered for serialization`() {
        val serialization = Repository.read(navigationSerialization)
        val keys = Regex("""data (?:object|class) (\w+)""").findAll(Repository.read(navigationKeys))
            .map { it.groupValues[1] }
            .toList()
        val violations = keys.filterNot { "subclass($it::class)" in serialization }
            .map { "$it is not registered in navKeySerializersModule" }
        assertNoViolations("back stack survives process death on every platform", violations)
    }
}
