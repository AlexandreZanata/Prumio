package app.prumio.tooling.architecturetests

import kotlin.test.Test

/**
 * Links docs/domain/business-rules.md to code (ADR-0010):
 * - rule ids are unique;
 * - code and tests only cite rules that exist;
 * - every rule marked `implemented` is cited by at least one test.
 */
class BusinessRuleTraceabilityTest {
    private val ruleId = Regex("""BR-[A-Z]+-\d{3}""")
    private val document = Repository.read("docs/domain/business-rules.md")

    private data class Rule(val id: String, val status: String)

    private val rules: List<Rule> = run {
        val headings = Regex("""^#{3,4} (BR-[A-Z]+-\d{3})\b""", RegexOption.MULTILINE).findAll(document).toList()
        headings.mapIndexed { index, heading ->
            val sectionEnd = headings.getOrNull(index + 1)?.range?.first ?: document.length
            val section = document.substring(heading.range.first, sectionEnd)
            val status = Regex("""\*\*Status:\*\*\s*(\w+)""").find(section)?.groupValues?.get(1) ?: "missing"
            Rule(id = heading.groupValues[1], status = status)
        }
    }

    @Test
    fun `rule ids are unique and have a valid status`() {
        val duplicates = rules.groupBy {
            it.id
        }.filterValues { it.size > 1 }.keys.map { "$it is defined more than once" }
        val invalidStatus = rules.filter { it.status !in setOf("planned", "implemented") }
            .map { "${it.id} has status '${it.status}' (use planned or implemented)" }
        assertNoViolations("business rule catalog", duplicates + invalidStatus)
    }

    @Test
    fun `code and tests cite only existing rules`() {
        val known = rules.map { it.id }.toSet()
        val violations = (Repository.appKotlinFiles + Repository.toolingTestFiles).flatMap { file ->
            val unknown = ruleId.findAll(file.text).map { it.value }.filterNot { it in known }.distinct()
            unknown.map { "${file.path} cites unknown $it" }
        }
        assertNoViolations("no dangling rule references", violations)
    }

    @Test
    fun `every implemented rule is covered by a test`() {
        val testFiles = Repository.appTestFiles + Repository.toolingTestFiles
        val cited = testFiles.flatMap { file -> ruleId.findAll(file.text).map { it.value } }.toSet()
        val violations = rules.filter { it.status == "implemented" && it.id !in cited }
            .map { "${it.id} is implemented but no test name or comment cites it" }
        assertNoViolations("implemented rules are proven by tests", violations)
    }
}
