package app.prumio.core.domain.reminder

import app.prumio.core.model.Settings
import app.prumio.core.testing.assertSuccess
import app.prumio.core.testing.assertValidation
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReminderPolicyTest {
    @Test fun `BR-TASK-006 setting a due time adds at-time but an explicit empty selection removes it`() {
        assertEquals(listOf(0), ReminderPolicy.taskOffsets(LocalTime(10, 0), null, emptyList(), null))
        assertEquals(emptyList(), ReminderPolicy.taskOffsets(LocalTime(10, 0), null, emptyList(), emptyList()))
        assertEquals(emptyList(), ReminderPolicy.taskOffsets(null, null, emptyList(), null))
        assertEquals(listOf(30), ReminderPolicy.taskOffsets(LocalTime(10, 0), null, listOf(30), null))
        assertEquals(emptyList(), ReminderPolicy.taskOffsets(LocalTime(11, 0), LocalTime(10, 0), emptyList(), null))
    }

    @Test fun `BR-APPT-004 appointment defaults distinguish timed and all-day`() {
        assertEquals(listOf(30), ReminderPolicy.appointmentOffsets(false, Settings()))
        assertEquals(listOf(0), ReminderPolicy.appointmentOffsets(true, Settings()))
    }

    @Test fun `BR-REM-018 offsets are unique nonnegative and limited to five`() {
        assertEquals(listOf(0, 30), normalizedOffsets(listOf(30, 0, 30)).assertSuccess())
        normalizedOffsets(listOf(-1)).assertValidation("BR-REM-018")
        normalizedOffsets((0..5).toList()).assertValidation("BR-REM-018")
        assertTrue(ReminderPolicy.presets.contains(10080))
    }
}
