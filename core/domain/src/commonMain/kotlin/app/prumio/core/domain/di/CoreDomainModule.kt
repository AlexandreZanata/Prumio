package app.prumio.core.domain.di

import app.prumio.core.domain.today.GetDayPeriodUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/** Registers every use case. Use cases are stateless: always `factoryOf`. */
val coreDomainModule = module {
    factoryOf(::GetDayPeriodUseCase)
}
