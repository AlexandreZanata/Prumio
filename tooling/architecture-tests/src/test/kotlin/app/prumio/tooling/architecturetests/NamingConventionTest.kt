package app.prumio.tooling.architecturetests

import com.lemonappdev.konsist.api.Konsist
import kotlin.test.Test

/** Naming and placement conventions (docs/architecture/conventions.md). */
class NamingConventionTest {
    private val production = Konsist.scopeFromProduction()

    @Test
    fun `use cases live in core domain and expose exactly one operator invoke`() {
        val violations = production.classes()
            .filter { it.name.endsWith("UseCase") }
            .mapNotNull { useCase ->
                val publicFunctions = useCase.functions().filter { it.hasPublicOrDefaultModifier }
                when {
                    !useCase.path.contains("/core/domain/") -> "${useCase.name} must live in :core:domain"

                    publicFunctions.map {
                        it.name
                    } != listOf("invoke") -> "${useCase.name} must expose only `operator fun invoke`"

                    !publicFunctions.single().hasOperatorModifier -> "${useCase.name}.invoke must be an operator"

                    else -> null
                }
            }
        assertNoViolations("one use case = one business operation", violations)
    }

    @Test
    fun `screen ViewModels extend PrumioViewModel and stay internal to their feature`() {
        val violations = production.classes()
            .filter { it.name.endsWith("ViewModel") && it.name != "PrumioViewModel" }
            .mapNotNull { viewModel ->
                when {
                    !viewModel.path.contains("/feature/") -> "${viewModel.name} must live in a feature module"

                    viewModel.parents().none { it.name.substringBefore('<') == "PrumioViewModel" } ->
                        "${viewModel.name} must extend PrumioViewModel"

                    !viewModel.hasInternalModifier -> "${viewModel.name} must be internal"

                    else -> null
                }
            }
        assertNoViolations("presentation pattern (docs/architecture/presentation-pattern.md)", violations)
    }

    @Test
    fun `every screen ViewModel has its Contract, Route and Screen files`() {
        val files = Repository.appProductionFiles.map { it.path }.toSet()
        val violations = files
            .filter { it.contains("/feature/") && it.endsWith("ViewModel.kt") }
            .flatMap { viewModelPath ->
                val prefix = viewModelPath.removeSuffix("ViewModel.kt")
                listOf("Contract.kt", "Route.kt", "Screen.kt")
                    .map { prefix + it }
                    .filterNot { it in files }
                    .map { "missing $it" }
            }
        assertNoViolations("screen file set: XxxContract, XxxViewModel, XxxRoute, XxxScreen", violations)
    }

    @Test
    fun `repository interfaces live in domain and implementations in data`() {
        val interfaceViolations = production.interfaces()
            .filter { it.name.endsWith("Repository") && !it.path.contains("/core/domain/") }
            .map { "${it.name} (interface) must live in :core:domain" }
        val classViolations = production.classes()
            .filter {
                it.name.endsWith("Repository") && !it.path.contains("/core/data/") &&
                    !it.path.contains("/core/testing/")
            }
            .map { "${it.name} (implementation) must live in :core:data" }
        assertNoViolations(
            "ports and adapters (docs/architecture/data-layer.md)",
            interfaceViolations + classViolations,
        )
    }

    @Test
    fun `fakes live in core testing`() {
        val violations = production.classes()
            .filter { it.name.startsWith("Fake") && !it.path.contains("/core/testing/") }
            .map { "${it.name} must live in :core:testing" }
        assertNoViolations("one shared set of test doubles", violations)
    }
}
