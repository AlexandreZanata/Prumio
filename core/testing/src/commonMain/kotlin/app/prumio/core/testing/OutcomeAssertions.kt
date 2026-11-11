package app.prumio.core.testing

import app.prumio.core.common.Outcome
import app.prumio.core.domain.error.DomainError
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** The success value, or a test failure showing the error. */
fun <T> Outcome<T, DomainError>.assertSuccess(): T = when (this) {
    is Outcome.Success -> value
    is Outcome.Failure -> throw AssertionError("Expected success but was $error")
}

/** The failure error, or a test failure. */
fun Outcome<*, DomainError>.assertFailure(): DomainError = assertIs<Outcome.Failure<DomainError>>(this).error

/** Asserts a validation failure of [ruleId] and returns it. */
fun Outcome<*, DomainError>.assertValidation(ruleId: String): DomainError.Validation {
    val error = assertIs<DomainError.Validation>(assertFailure())
    assertEquals(ruleId, error.ruleId)
    return error
}

/** Asserts an invalid state transition citing [ruleId]. */
fun Outcome<*, DomainError>.assertInvalidTransition(ruleId: String): DomainError.InvalidTransition {
    val error = assertIs<DomainError.InvalidTransition>(assertFailure())
    assertEquals(ruleId, error.ruleId)
    return error
}

fun Outcome<*, DomainError>.assertNotFound(): DomainError.NotFound = assertIs<DomainError.NotFound>(assertFailure())
