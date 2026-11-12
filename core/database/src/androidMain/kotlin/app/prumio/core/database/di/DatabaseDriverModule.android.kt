package app.prumio.core.database.di

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.prumio.core.database.PrumioDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseDriverModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = PrumioDatabase.Schema,
            context = androidContext(),
            name = DATABASE_FILE_NAME,
            callback = object : AndroidSqliteDriver.Callback(PrumioDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    ensurePreReleaseReminderTables(db)
                }

                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
        )
    }
}
