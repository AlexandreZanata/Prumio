package app.prumio.core.databasetesting

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.prumio.core.database.PrumioDatabase
import java.util.Properties

/**
 * A fresh, empty in-memory [PrumioDatabase] with the production schema and foreign keys enabled,
 * for JVM host tests. Close it with [InMemoryDatabase.close] (or let the test end).
 */
class InMemoryDatabase {
    private val driver = JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY,
        properties = Properties().apply { put("foreign_keys", "true") },
    ).also { PrumioDatabase.Schema.create(it) }

    val database: PrumioDatabase = PrumioDatabase(driver)

    fun close() {
        driver.close()
    }
}
