package app.prumio.core.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.prumio.core.database.di.ensurePreReleaseReminderTables
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 36])
class PreReleaseReminderTablesTest {
    @Test fun `pre-release v1 bridge is additive idempotent and preserves installed records`() {
        val context: Context = RuntimeEnvironment.getApplication()
        val driver = AndroidSqliteDriver(
            PrumioDatabase.Schema,
            context,
            name = null,
            callback = object : AndroidSqliteDriver.Callback(PrumioDatabase.Schema) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE sentinel (value TEXT NOT NULL)")
                    db.execSQL("INSERT INTO sentinel VALUES ('preserved')")
                }
                override fun onOpen(db: SupportSQLiteDatabase) {
                    ensurePreReleaseReminderTables(db)
                    ensurePreReleaseReminderTables(db)
                    db.query("SELECT value FROM sentinel").use { row ->
                        row.moveToFirst()
                        assertEquals("preserved", row.getString(0))
                    }
                    val sql = "SELECT count(*) FROM sqlite_master WHERE type = 'table' " +
                        "AND name IN ('reminder', 'scheduled_alarm', 'snooze', 'delivered_alarm')"
                    db.query(sql).use { row ->
                        row.moveToFirst()
                        assertEquals(4, row.getInt(0))
                    }
                }
            },
        )
        driver.use { assertEquals(emptyList(), PrumioDatabase(it).reminderQueries.selectAll().executeAsList()) }
    }
}
