package app.prumio.core.database

import app.prumio.core.databasetesting.InMemoryDatabase
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

/** Constraints, cascades and ordering of schema version 1 (docs/architecture/database-schema.md). */
class SchemaTest {
    private val db = InMemoryDatabase()
    private val database = db.database

    @AfterTest
    fun tearDown() = db.close()

    private fun insertMatter(id: String = "m1") = database.matterQueries.insert(
        id, "Car documents", null, "OPEN", null, null, null, null, 1L, "car documents", 1L, 1L,
    )

    private fun insertTask(
        id: String,
        status: String = "OPEN",
        dueDate: String? = null,
        dueTime: String? = null,
        matterId: String? = null,
        createdAt: Long = 1L,
    ) = database.taskQueries.insert(
        id, "Task $id", null, status, dueDate, dueTime, null, null, matterId, null, null, "task", createdAt, createdAt,
    )

    @Test
    fun `inbox items are listed newest first`() {
        database.inbox_itemQueries.insert("a", "first", "first", 1L, 1L)
        database.inbox_itemQueries.insert("b", "second", "second", 2L, 2L)

        assertEquals(listOf("b", "a"), database.inbox_itemQueries.selectAll().executeAsList().map { it.id })
        assertEquals(2L, database.inbox_itemQueries.countAll().executeAsOne())
    }

    @Test
    fun `task constraints reject invalid rows`() {
        assertFails { insertTask("t1", status = "DONE") }
        assertFails { insertTask("t2", dueTime = "09:00") }
    }

    @Test
    fun `open tasks due on or before a date exclude undated and closed tasks`() {
        insertTask("past", dueDate = "2026-11-10")
        insertTask("today", dueDate = "2026-11-11", dueTime = "09:00")
        insertTask("future", dueDate = "2026-11-12")
        insertTask("undated")
        insertTask("done", status = "COMPLETED", dueDate = "2026-11-10")

        val ids = database.taskQueries.selectOpenDueOnOrBefore("2026-11-11").executeAsList().map { it.id }

        assertEquals(listOf("past", "today"), ids)
        assertEquals(listOf("undated"), database.taskQueries.selectOpenUndated().executeAsList().map { it.id })
    }

    @Test
    fun `deleting a matter deletes its history and unlinks its tasks`() {
        insertMatter()
        database.matter_entryQueries.insert("e1", "m1", "CREATED", null, null, null, null, "2026-11-11", "", 1L, 1L)
        insertTask("t1", matterId = "m1")

        database.matterQueries.deleteById("m1")

        assertNull(database.matter_entryQueries.selectById("e1").executeAsOneOrNull())
        assertNull(database.taskQueries.selectById("t1").executeAsOne().matter_id)
    }

    @Test
    fun `appointment timing columns must be consistent`() {
        val q = database.appointmentQueries
        assertFails {
            q.insert("a1", "x", null, null, 1L, "2026-11-12", "10:00", "2026-11-12", null, null, null, null, "", 1L, 1L)
        }
        assertFails {
            q.insert("a2", "x", null, null, 0L, "2026-11-12", null, "2026-11-12", null, "UTC", null, null, "", 1L, 1L)
        }
        assertFails {
            q.insert("a3", "x", null, null, 1L, "2026-11-12", null, "2026-11-11", null, null, null, null, "", 1L, 1L)
        }
    }

    @Test
    fun `overlapping appointments are found by stored dates`() {
        val q = database.appointmentQueries
        q.insert("trip", "Trip", null, null, 1L, "2026-11-10", null, "2026-11-14", null, null, null, null, "", 1L, 1L)
        q.insert("call", "Call", null, null, 0L, "2026-11-12", "16:00", "2026-11-12", null, "UTC", null, null, "", 1, 1)
        q.insert("later", "Later", null, null, 1L, "2026-11-20", null, "2026-11-20", null, null, null, null, "", 1L, 1L)

        assertEquals(
            listOf("trip", "call"),
            q.selectOverlapping(fromDate = "2026-11-12", toDate = "2026-11-13").executeAsList().map {
                it.id
            },
        )
    }

    @Test
    fun `matters match on their notes`() {
        insertMatter()
        database.matter_entryQueries.insert(
            "n1", "m1", "NOTE", "Called dispatcher", null, null, null, "2026-11-11", "called dispatcher", 1L, 1L,
        )

        assertEquals(listOf("m1"), database.matterQueries.selectIdsMatching("dispatcher").executeAsList())
        assertEquals(listOf("m1"), database.matterQueries.selectIdsMatching("car").executeAsList())
    }
}
