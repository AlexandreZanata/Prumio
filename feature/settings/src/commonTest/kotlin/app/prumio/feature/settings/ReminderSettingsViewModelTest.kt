package app.prumio.feature.settings

import app.cash.turbine.test
import app.prumio.core.common.Outcome
import app.prumio.core.domain.reminder.NotificationAccessState
import app.prumio.core.domain.reminder.SnoozeOption
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.navigation.SnoozeKey
import app.prumio.core.testing.ReminderTestEnvironment
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.settings.reliability.ReliabilityTarget
import app.prumio.feature.settings.reliability.ReminderReliabilityAction
import app.prumio.feature.settings.reliability.ReminderReliabilityEffect
import app.prumio.feature.settings.reliability.ReminderReliabilityViewModel
import app.prumio.feature.settings.snooze.SnoozeAction
import app.prumio.feature.settings.snooze.SnoozeEffect
import app.prumio.feature.settings.snooze.SnoozeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ReminderSettingsViewModelTest {
    private val env = ReminderTestEnvironment()
    private val domain = env.domain

    @Test fun `BR-SET-002 reminder defaults are updated through the domain`() = runViewModelTest {
        val vm = SettingsViewModel(domain.observeSettings, domain.updateSetting, domain.getReminderReliability)
        vm.onAction(SettingsAction.ChangeReminderTime(LocalTime(8, 30)))
        vm.onAction(SettingsAction.ChangeAppointmentNotice(60))
        assertEquals(LocalTime(8, 30), vm.state.value.defaultReminderTime)
        assertEquals(60, vm.state.value.defaultAppointmentNoticeMinutes)
        vm.effects.test {
            vm.onAction(SettingsAction.OpenReliability)
            assertEquals(SettingsEffect.OpenReliability, awaitItem())
        }
    }

    @Test fun `BR-REM-014 reliability refreshes and dismisses denied banner`() = runViewModelTest {
        domain.createTask(TaskDraft("Call", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(10, 0)))
        domain.notificationAccess.current = NotificationAccessState(false, true, false, true)
        val settings = SettingsViewModel(domain.observeSettings, domain.updateSetting, domain.getReminderReliability)
        assertTrue(settings.state.value.showDeniedBanner)
        settings.onAction(SettingsAction.DismissBanner)
        assertFalse(settings.state.value.showDeniedBanner)
        domain.recordNotificationPermission(false)
        settings.onAction(SettingsAction.Refresh)
        assertTrue(settings.state.value.showDeniedBanner)
        val vm = ReminderReliabilityViewModel(env.reliability, domain.updateSetting)
        assertTrue(vm.state.value.status!!.showDeniedBanner)
        vm.onAction(ReminderReliabilityAction.DismissBanner)
        assertFalse(vm.state.value.status!!.showDeniedBanner)
        domain.notificationAccess.current = NotificationAccessState(true, false, true, false)
        vm.onAction(ReminderReliabilityAction.Refresh)
        assertTrue(vm.state.value.status!!.access.notificationsAllowed)
        vm.effects.test {
            ReliabilityTarget.entries.forEach { target ->
                vm.onAction(ReminderReliabilityAction.Fix(target))
                assertEquals(ReminderReliabilityEffect.OpenSettings(target), awaitItem())
            }
            vm.onAction(ReminderReliabilityAction.Close)
            assertEquals(ReminderReliabilityEffect.Close, awaitItem())
        }
    }

    @Test fun `BR-REM-007 snooze sheet persists choice and reports stale key`() = runViewModelTest {
        domain.createTask(TaskDraft("Call", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(10, 0)))
        env.reconcile()
        val key = env.scheduled.all().single().key
        val vm = SnoozeViewModel(SnoozeKey(key.value), env.snooze)
        assertEquals(SnoozeOption.entries, vm.state.value.options)
        vm.effects.test {
            vm.onAction(SnoozeAction.Select(SnoozeOption.OneHour))
            assertIs<SnoozeEffect.ShowMessage>(awaitItem())
            assertEquals(SnoozeEffect.Close, awaitItem())
        }
        assertTrue(key in env.snoozes.all())
        val stale = SnoozeViewModel(SnoozeKey("task:missing:0:0"), env.snooze)
        stale.effects.test {
            stale.onAction(SnoozeAction.Select(SnoozeOption.OneHour))
            assertIs<SnoozeEffect.ShowMessage>(awaitItem())
            assertFalse(stale.state.value.isSaving)
            stale.onAction(SnoozeAction.Close)
            assertEquals(SnoozeEffect.Close, awaitItem())
        }
    }
}
