package app.prumio.core.designsystem.component

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrumioBadgeTest {
    @Test
    fun `no badge for zero or negative counts`() {
        assertNull(badgeText(0))
        assertNull(badgeText(-1))
    }

    @Test
    fun `counts up to 99 are shown as is`() {
        assertEquals("1", badgeText(1))
        assertEquals("99", badgeText(99))
    }

    @Test
    fun `larger counts are capped`() {
        assertEquals("99+", badgeText(100))
    }
}
