package app.prumio.core.data

import app.prumio.core.databasetesting.InMemoryDatabase
import app.prumio.core.testing.TestDispatcherProvider
import kotlin.test.AfterTest

/** Shared setup: a fresh in-memory database per test and a test dispatcher. */
abstract class RepositoryTestBase {
    private val inMemory = InMemoryDatabase()
    protected val database = inMemory.database
    protected val dispatchers = TestDispatcherProvider()

    @AfterTest
    fun closeDatabase() = inMemory.close()
}
