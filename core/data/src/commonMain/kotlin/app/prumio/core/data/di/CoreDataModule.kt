package app.prumio.core.data.di

import org.koin.dsl.module

/**
 * Binds every domain port (repository interface) to its implementation, e.g.
 * `singleOf(::SqlDelightTaskRepository) bind TaskRepository::class`.
 * Recipe: docs/ai/recipes/add-repository.md. First bindings arrive in phase 1.
 */
val coreDataModule = module {
}
