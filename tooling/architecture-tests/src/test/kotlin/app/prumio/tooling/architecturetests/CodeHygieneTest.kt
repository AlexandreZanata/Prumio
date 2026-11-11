package app.prumio.tooling.architecturetests

import kotlin.test.Test

/** Small rules that keep the code predictable for humans and AI agents (docs/architecture/conventions.md). */
class CodeHygieneTest {
    private val production = Repository.appProductionFiles

    @Test
    fun `production code avoids unsafe or untestable constructs`() {
        val forbidden = mapOf(
            "GlobalScope" to "use an injected or lifecycle-bound scope",
            "runBlocking" to "suspend instead of blocking",
            "println(" to "do not log user data; there is no console in production",
            "!!" to "handle null explicitly",
            "Clock.System" to "inject TimeProvider",
            "TimeZone.currentSystemDefault()" to "inject TimeProvider",
            "Uuid.random()" to "inject IdGenerator",
        )
        val allowedFiles = setOf(
            "core/common/src/commonMain/kotlin/app/prumio/core/common/time/TimeProvider.kt",
            "core/common/src/commonMain/kotlin/app/prumio/core/common/id/IdGenerator.kt",
        )
        val violations = production
            .filterNot { it.path in allowedFiles }
            .flatMap { file ->
                forbidden.filterKeys {
                    file.text.contains(it)
                }.map { (token, fix) -> "${file.path} uses `$token`: $fix" }
            }
        assertNoViolations("no hidden global state, blocking calls or crashes-by-design", violations)
    }

    @Test
    fun `lists never use removeFirst or removeLast`() {
        val pattern = Regex("""\.remove(First|Last)\(\)""")
        val violations = Repository.appKotlinFiles
            .filter { pattern.containsMatchIn(it.text) }
            .map { "${it.path}: use removeAt(0) / removeAt(lastIndex) (crashes on Android 14 and older)" }
        assertNoViolations("Android API compatibility", violations)
    }

    @Test
    fun `source files stay small enough to read in one go`() {
        val maxLines = 400
        val violations = production
            .filter { it.lineCount > maxLines }
            .map { "${it.path} has ${it.lineCount} lines (max $maxLines): split it by responsibility" }
        assertNoViolations("small files", violations)
    }

    @Test
    fun `package matches the module and directory`() {
        val violations = Repository.appKotlinFiles
            .filterNot { file ->
                val expectedPrefix = "app.prumio." + file.module.replace('/', '.').replace("-", "")
                val directory = file.path.substringAfter("/kotlin/").substringBeforeLast('/').replace('/', '.')
                file.packageName.startsWith(expectedPrefix) && file.packageName == directory
            }
            .map { "${it.path} declares package '${it.packageName}'" }
        assertNoViolations("package = app.prumio.<module path>.<subpackage> and mirrors the folders", violations)
    }
}
