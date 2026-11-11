package app.prumio.core.domain.error

/**
 * Expected failures returned by use cases inside `Outcome.Failure`.
 * Taxonomy and user-facing mapping: docs/architecture/error-handling.md.
 */
sealed interface DomainError {
    /** Input broke a business rule. [ruleId] points to docs/domain/business-rules.md (e.g. "BR-TASK-001"). */
    data class Validation(val ruleId: String, val field: String, val reason: String) : DomainError

    /** The entity was deleted or never existed. */
    data class NotFound(val entity: String, val id: String) : DomainError

    /** The requested state transition is not allowed (see docs/domain/state-machines.md). */
    data class InvalidTransition(val ruleId: String, val from: String, val to: String) : DomainError

    /** Local storage failed (disk full, corrupted database, I/O error). */
    data class Storage(val cause: Throwable) : DomainError
}
