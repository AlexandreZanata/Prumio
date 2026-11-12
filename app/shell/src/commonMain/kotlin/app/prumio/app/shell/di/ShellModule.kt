package app.prumio.app.shell.di

import app.prumio.app.shell.ShellViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Application chrome state, scoped by ShellRoute to the root lifecycle. */
internal val shellModule = module { viewModelOf(::ShellViewModel) }
