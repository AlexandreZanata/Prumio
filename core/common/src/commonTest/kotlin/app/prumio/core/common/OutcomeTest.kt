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

    @Test
    fun `valueOr returns the value or runs the failure branch`() {
        fun firstChar(text: String): Outcome<Char, String> = text.firstOrNull()?.asSuccess() ?: "empty".asFailure()
        fun describe(text: String): String {
            val char = firstChar(text).valueOr { return "failed: $it" }
            return "starts with $char"
        }

        assertEquals("starts with a", describe("abc"))
        assertEquals("failed: empty", describe(""))
        assertEquals('a', firstChar("a").valueOrNull())
        assertEquals(null, firstChar("").valueOrNull())
    }

    @Test
    fun `callbacks run only for their branch`() {
        var seen = ""
        1.asSuccess().onSuccess { seen += "s$it" }.onFailure { seen += "f" }
        "x".asFailure().onSuccess { seen += "s" }.onFailure { seen += "f$it" }

        assertEquals("s1fx", seen)
    }
}
