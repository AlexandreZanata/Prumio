package app.prumio.core.database.di

import app.prumio.core.database.PrumioDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/** File name of the SQLite database inside app-private storage. Never rename (it would lose user data). */
const val DATABASE_FILE_NAME = "prumio.db"

/** Provides the platform [app.cash.sqldelight.db.SqlDriver]. */
internal expect val databaseDriverModule: Module

val coreDatabaseModule = module {
    includes(databaseDriverModule)
    single { PrumioDatabase(driver = get()) }
}
