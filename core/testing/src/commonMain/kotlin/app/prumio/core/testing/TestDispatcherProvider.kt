package app.prumio.core.testing

import app.prumio.core.common.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/** Runs repository work on a test dispatcher. */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()) : DispatcherProvider {
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
