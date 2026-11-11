package app.prumio.core.testing

import app.prumio.core.common.id.IdGenerator

/** Deterministic UUID-shaped ids: 00000000-0000-0000-0000-000000000001, ...002, ... */
class FakeIdGenerator : IdGenerator {
    private var counter = 0

    override fun newId(): String {
        counter += 1
        return "00000000-0000-0000-0000-" + counter.toString().padStart(length = 12, padChar = '0')
    }
}
