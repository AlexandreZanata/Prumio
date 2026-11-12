package app.prumio.core.ui.mvi

import android.content.Intent
import android.os.Looper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnTimeChangeTest {
    @Test
    fun `BR-TODAY-007 clock date and time-zone changes refresh while the screen is open`() {
        val context = RuntimeEnvironment.getApplication()
        var changes = 0
        val stop = observeTimeChanges(context) { changes++ }
        try {
            listOf(Intent.ACTION_TIME_CHANGED, Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED)
                .forEach { action ->
                    context.sendBroadcast(Intent(action))
                    shadowOf(Looper.getMainLooper()).idle()
                }
            assertEquals(3, changes)
        } finally {
            stop()
        }
    }

    @Test
    fun `BR-TIME-003 time observer is removed when the screen leaves composition`() {
        val context = RuntimeEnvironment.getApplication()
        var changes = 0
        val stop = observeTimeChanges(context) { changes++ }
        stop()
        context.sendBroadcast(Intent(Intent.ACTION_TIME_CHANGED))
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(0, changes)
    }
}
