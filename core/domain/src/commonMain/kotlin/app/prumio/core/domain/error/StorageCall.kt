package app.prumio.core.domain.error

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import kotlinx.coroutines.CancellationException

/**
 * Runs a storage operation and turns I/O failures into [DomainError.Storage]
 * (docs/architecture/error-handling.md). Cancellation is never swallowed.
 */
@Suppress("TooGenericExceptionCaught") // Any storage failure must reach the user as a calm message.
internal inline fun <T> storageCall(block: () -> T): Outcome<T, DomainError> = try {
    block().asSuccess()
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (failure: Exception) {
    DomainError.Storage(failure).asFailure()
}

internal fun notFound(entity: String, id: String): Outcome<Nothing, DomainError> =
    DomainError.NotFound(entity, id).asFailure()

internal fun invalidTransition(ruleId: String, from: Any, to: Any): Outcome<Nothing, DomainError> =
    DomainError.InvalidTransition(ruleId, from.toString(), to.toString()).asFailure()
