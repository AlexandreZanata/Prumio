package app.prumio.tooling.architecturetests

import java.io.File

/** Read-only view of the repository used by the architecture tests. */
internal object Repository {
    val root: File = File(
        System.getProperty("prumio.repositoryRoot")
            ?: error("prumio.repositoryRoot is not set. Run these tests through Gradle."),
    )

    private val appRoots = listOf("app/", "core/", "feature/")
    private val ignoredDirectories = setOf("build", ".gradle", ".kotlin", ".git", ".idea")

    fun read(relativePath: String): String = File(root, relativePath).readText()

    fun exists(relativePath: String): Boolean = File(root, relativePath).exists()

    private val allKotlinFiles: List<SourceFile> by lazy {
        root.walkTopDown()
            .onEnter { it.name !in ignoredDirectories }
            .filter { it.isFile && it.extension == "kt" }
            .map { SourceFile(path = it.relativeTo(root).invariantSeparatorsPath, text = it.readText()) }
            .toList()
    }

    /** Kotlin sources of the shipped app (app/, core/, feature/), production and tests. */
    val appKotlinFiles: List<SourceFile> by lazy {
        allKotlinFiles.filter { file -> appRoots.any { file.path.startsWith(it) } }
    }

    val appProductionFiles: List<SourceFile> by lazy { appKotlinFiles.filterNot { it.isTest } }

    val appTestFiles: List<SourceFile> by lazy { appKotlinFiles.filter { it.isTest } }

    /** Tests of tooling/ modules (they prove repository-level rules such as privacy). */
    val toolingTestFiles: List<SourceFile> by lazy {
        allKotlinFiles.filter { it.path.startsWith("tooling/") && it.isTest }
    }

    /** Gradle module directories declared in settings.gradle.kts, e.g. "core/domain". */
    val modules: List<String> by lazy {
        Regex("""include\("(:[^"]+)"\)""").findAll(read("settings.gradle.kts"))
            .map { it.groupValues[1].removePrefix(":").replace(':', '/') }
            .toList()
    }
}

internal data class SourceFile(val path: String, val text: String) {
    /** Module directory, e.g. "feature/today". */
    val module: String get() = path.substringBefore("/src/")

    /** Source set name, e.g. "commonMain", "androidMain", "commonTest". */
    val sourceSet: String get() = path.substringAfter("/src/").substringBefore('/')

    val isTest: Boolean get() = sourceSet.contains("test", ignoreCase = true)

    val isCommonMain: Boolean get() = sourceSet == "commonMain"

    val packageName: String get() = text.lineSequence().firstOrNull {
        it.startsWith("package ")
    }?.removePrefix("package ")?.trim().orEmpty()

    val imports: List<String>
        get() = text.lineSequence()
            .filter { it.startsWith("import ") }
            .map { it.removePrefix("import ").substringBefore(" as ").trim() }
            .toList()

    val lineCount: Int get() = text.lines().size
}

/** Fails with every offender listed, so an agent can fix all of them in one pass. */
internal fun assertNoViolations(rule: String, violations: List<String>) {
    if (violations.isNotEmpty()) {
        throw AssertionError(
            buildString {
                appendLine("Architecture rule violated: $rule")
                violations.sorted().forEach { appendLine("  - $it") }
            },
        )
    }
}
