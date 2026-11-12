package app.prumio.app.shell.di

import android.content.Context
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

class KoinModulesTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `every definition in prumioModules can be created`() {
        module { includes(prumioModules) }.verify(
            extraTypes = listOf(
                Context::class,
                TaskEditorKey::class,
                TaskDetailKey::class,
                AllTasksKey::class,
                AppointmentEditorKey::class,
                AppointmentDetailKey::class,
                MatterEditorKey::class,
                MatterDetailKey::class,
            ),
        )
    }
}
