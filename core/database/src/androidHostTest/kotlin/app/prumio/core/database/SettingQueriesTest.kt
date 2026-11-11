package app.prumio.core.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingQueriesTest {
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { PrumioDatabase.Schema.create(it) }
    private val queries = PrumioDatabase(driver).settingQueries

    @AfterTest
    fun tearDown() {
        driver.close()
    }

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
