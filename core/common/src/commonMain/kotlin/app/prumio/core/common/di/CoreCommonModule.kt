package app.prumio.core.common.di

import app.prumio.core.common.coroutines.DefaultDispatcherProvider
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.id.UuidIdGenerator
import app.prumio.core.common.time.SystemTimeProvider
import app.prumio.core.common.time.TimeProvider
import org.koin.dsl.module

val coreCommonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<IdGenerator> { UuidIdGenerator() }
    single<TimeProvider> { SystemTimeProvider() }
}
