package app.prumio.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Runs a ViewModel test with `Dispatchers.Main` replaced by an unconfined test dispatcher,
 * so `viewModelScope` work runs eagerly and deterministically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runViewModelTest(testBody: suspend TestScope.() -> Unit): TestResult {
    val dispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(dispatcher)
    return try {
        runTest(dispatcher) { testBody() }
    } finally {
        Dispatchers.resetMain()
    }
}
