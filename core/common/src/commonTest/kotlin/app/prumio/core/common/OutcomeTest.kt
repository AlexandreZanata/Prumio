package app.prumio.core.common

import kotlin.test.Test
import kotlin.test.assertEquals

class OutcomeTest {
    @Test
    fun `map transforms a success and keeps a failure`() {
        val success: Outcome<Int, String> = 2.asSuccess()
        val failure: Outcome<Int, String> = "boom".asFailure()

        assertEquals(Outcome.Success(4), success.map { it * 2 })
        assertEquals(Outcome.Failure("boom"), failure.map { it * 2 })
    }

    @Test
    fun `flatMap chains only successes`() {
        val result = 2.asSuccess().flatMap { value -> if (value > 1) "ok".asSuccess() else "small".asFailure() }

        assertEquals(Outcome.Success("ok"), result)
    }
}
