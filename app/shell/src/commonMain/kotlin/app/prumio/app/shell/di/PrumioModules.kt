package app.prumio.app.shell.di

import app.prumio.core.common.di.coreCommonModule
import app.prumio.core.data.di.coreDataModule
import app.prumio.core.database.di.coreDatabaseModule
import app.prumio.core.domain.di.coreDomainModule
import app.prumio.core.notifications.di.coreNotificationsModule
import app.prumio.feature.agenda.agendaFeatureModule
import app.prumio.feature.capture.captureFeatureModule
import app.prumio.feature.inbox.inboxFeatureModule
import app.prumio.feature.matters.mattersFeatureModule
import app.prumio.feature.search.searchFeatureModule
import app.prumio.feature.settings.settingsFeatureModule
import app.prumio.feature.tasks.tasksFeatureModule
import app.prumio.feature.today.todayFeatureModule
import org.koin.core.module.Module

/**
 * Every Koin module of the app. A new Gradle module that defines DI bindings must be added here,
 * otherwise its definitions are missing at runtime (KoinModulesTest catches it).
 */
val prumioModules: List<Module> = listOf(
    coreCommonModule,
    coreDatabaseModule,
    coreDataModule,
    coreDomainModule,
    coreNotificationsModule,
    agendaFeatureModule,
    captureFeatureModule,
    inboxFeatureModule,
    mattersFeatureModule,
    searchFeatureModule,
    settingsFeatureModule,
    tasksFeatureModule,
    todayFeatureModule,
)
