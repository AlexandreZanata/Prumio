package app.prumio.core.database

import app.prumio.core.databasetesting.InMemoryDatabase
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingQueriesTest {
    private val db = InMemoryDatabase()
    private val queries = db.database.settingQueries

    @AfterTest
    fun tearDown() = db.close()

    @Test
    fun `upsert inserts then replaces a value`() {
        queries.upsert("theme", "dark", 1L)
        queries.upsert("theme", "light", 2L)

        assertEquals("light", queries.selectByKey("theme").executeAsOne())
        assertEquals(1, queries.selectAll().executeAsList().size)
    }

    @Test
    fun `deleteByKey removes the value`() {
        queries.upsert("theme", "dark", 1L)

        queries.deleteByKey("theme")

        assertNull(queries.selectByKey("theme").executeAsOneOrNull())
    }
}
