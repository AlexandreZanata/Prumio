package app.prumio.tooling.architecturetests

import java.io.File
import kotlin.test.Test

/** Documentation is part of the architecture: relative links and anchors must resolve. */
class DocumentationLinksTest {
    private val ignoredDirectories = setOf("build", ".gradle", ".kotlin", ".git", ".idea")
    private val codeBlock = Regex("```.*?```", RegexOption.DOT_MATCHES_ALL)
    private val link = Regex("""\]\(([^)\s]+)\)""")
    private val heading = Regex("""^#{1,6}\s+(.*)$""", RegexOption.MULTILINE)

    private val markdownFiles: List<File> = Repository.root.walkTopDown()
        .onEnter { it.name !in ignoredDirectories }
        .filter { it.isFile && it.extension == "md" }
        .toList()

    @Test
    fun `relative links and anchors in markdown resolve`() {
        val violations = markdownFiles.flatMap { file ->
            val text = file.readText().replace(codeBlock, "")
            link.findAll(text).mapNotNull { match -> brokenLink(file, match.groupValues[1]) }.toList()
        }
        assertNoViolations("docs links resolve (fix the link or the heading)", violations)
    }

    private fun brokenLink(file: File, target: String): String? {
        if (Regex("^[a-z]+:").containsMatchIn(target)) return null
        val path = target.substringBefore('#')
        val anchor = target.substringAfter('#', missingDelimiterValue = "")
        val destination = if (path.isEmpty()) file else File(file.parentFile, path).normalize()
        val source = file.relativeTo(Repository.root).invariantSeparatorsPath
        return when {
            !destination.exists() -> "$source → $target (file not found)"

            anchor.isNotEmpty() && destination.extension == "md" && anchor !in anchorsOf(destination) ->
                "$source → $target (heading not found)"

            else -> null
        }
    }

    /** GitHub-style heading slugs. */
    private fun anchorsOf(file: File): Set<String> = heading.findAll(file.readText().replace(codeBlock, ""))
        .map { match ->
            match.groupValues[1].trim().lowercase()
                .replace(Regex("""[^\p{L}\p{N}_\s-]"""), "")
                .replace(' ', '-')
        }
        .toSet()
}
