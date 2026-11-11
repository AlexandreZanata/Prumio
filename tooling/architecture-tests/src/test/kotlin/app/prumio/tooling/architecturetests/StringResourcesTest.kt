package app.prumio.tooling.architecturetests

import java.io.File
import kotlin.test.Test

/** Compose resource strings are complete in every language and use Compose syntax (docs/architecture/i18n.md). */
class StringResourcesTest {
    private val ignoredDirectories = setOf("build", ".gradle", ".kotlin", ".git", ".idea")
    private val key = Regex("""<(?:string|plurals) name="([^"]+)"""")

    private val englishFiles: List<File> = Repository.root.walkTopDown()
        .onEnter { it.name !in ignoredDirectories }
        .filter { it.name == "strings.xml" && it.parentFile.name == "values" && it.path.contains("composeResources") }
        .toList()

    private fun File.relative() = relativeTo(Repository.root).invariantSeparatorsPath

    @Test
    fun `every string exists in English and Brazilian Portuguese`() {
        val violations = englishFiles.flatMap { english ->
            val portuguese = File(english.parentFile.parentFile, "values-pt-rBR/strings.xml")
            if (!portuguese.exists()) return@flatMap listOf("${portuguese.relative()} is missing")
            val englishKeys = key.findAll(english.readText()).map { it.groupValues[1] }.toSet()
            val portugueseKeys = key.findAll(portuguese.readText()).map { it.groupValues[1] }.toSet()
            (englishKeys - portugueseKeys).map { "${portuguese.relative()} lacks '$it'" } +
                (portugueseKeys - englishKeys).map { "${english.relative()} lacks '$it'" }
        }
        assertNoViolations("translations are complete", violations)
    }

    @Test
    fun `strings do not use Android escaping`() {
        val violations = Repository.root.walkTopDown()
            .onEnter { it.name !in ignoredDirectories }
            .filter { it.name == "strings.xml" && it.path.contains("composeResources") }
            .filter { it.readText().contains("\\'") || it.readText().contains("\\\"") }
            .map { "${it.relative()} escapes quotes: write ' and \" directly in Compose resources" }
            .toList()
        assertNoViolations("Compose resources are not Android resources", violations)
    }
}
