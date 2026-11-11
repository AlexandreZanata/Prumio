package app.prumio.core.common.id

import kotlin.uuid.Uuid

/**
 * Creates stable, globally unique identifiers for new entities (ADR-0011).
 * Inject it instead of calling `Uuid.random()` so tests can use `FakeIdGenerator`.
 */
fun interface IdGenerator {
    fun newId(): String
}

class UuidIdGenerator : IdGenerator {
    override fun newId(): String = Uuid.random().toString()
}
