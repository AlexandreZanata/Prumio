package app.prumio.core.database.di

import androidx.sqlite.db.SupportSQLiteDatabase
import app.prumio.core.database.PrumioDatabase

/** Additive v1 pre-release bridge; reads the actual SQL sources, preserving all installed debug data. */
internal fun ensurePreReleaseReminderTables(database: SupportSQLiteDatabase) {
    if (PrumioDatabase.Schema.version != 1L) return
    listOf("reminder", "scheduled_alarm", "snooze", "delivered_alarm").forEach { table ->
        val resource =
            requireNotNull(PrumioDatabase::class.java.getResourceAsStream("/app/prumio/core/database/$table.sq"))
        val ddl = resource.bufferedReader().use { it.readText() }.substringBefore("\nselectAll:")
        ddl.split(';').filter { it.isNotBlank() }.forEach { statement ->
            database.execSQL(
                statement.replace(
                    "CREATE TABLE ",
                    "CREATE TABLE IF NOT EXISTS ",
                ).replace("CREATE INDEX ", "CREATE INDEX IF NOT EXISTS "),
            )
        }
    }
}
