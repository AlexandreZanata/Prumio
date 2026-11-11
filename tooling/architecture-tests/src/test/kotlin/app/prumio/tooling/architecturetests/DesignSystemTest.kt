package app.prumio.tooling.architecturetests

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import kotlin.test.Test

/**
 * Component reuse rules (ADR-0008, docs/design-system/components.md).
 * A composable used by two features must live in :core:designsystem or :core:ui, never be copied.
 */
class DesignSystemTest {
    private val composables: List<KoFunctionDeclaration> = Konsist.scopeFromProduction()
        .functions()
        .filter { it.hasAnnotationWithName("Composable") }

    private fun List<KoFunctionDeclaration>.publicIn(modulePath: String) = filter {
        it.path.contains("/$modulePath/src/commonMain/") && it.hasPublicOrDefaultModifier && it.isTopLevel
    }

    @Test
    fun `design system components are prefixed with Prumio`() {
        val violations = composables.publicIn("core/designsystem")
            .filterNot { it.name.startsWith("Prumio") }
            .map { "${it.name} in ${it.path.substringAfterLast('/')}" }
        assertNoViolations("design system components are named Prumio*", violations)
    }

    @Test
    fun `every shared composable is listed in the component catalog`() {
        val catalog = Repository.read("docs/design-system/components.md")
        val violations = (composables.publicIn("core/designsystem") + composables.publicIn("core/ui"))
            .filterNot { catalog.contains("`${it.name}`") }
            .map { "${it.name} is missing from docs/design-system/components.md" }
        assertNoViolations("the catalog is the index agents search before creating a component", violations)
    }

    @Test
    fun `features expose no public composables`() {
        val violations = composables
            .filter { it.path.contains("/feature/") && it.hasPublicOrDefaultModifier }
            .map { "${it.name} in ${it.path.substringAfter("/feature/")} must be internal or private" }
        assertNoViolations(
            "a composable needed outside its feature moves to :core:ui or :core:designsystem",
            violations,
        )
    }
}
