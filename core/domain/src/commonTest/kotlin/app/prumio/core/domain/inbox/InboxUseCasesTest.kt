package app.prumio.core.domain.inbox

import app.cash.turbine.test
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.testing.FakeIdGenerator
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.assertNotFound
import app.prumio.core.testing.assertSuccess
import app.prumio.core.testing.assertValidation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class InboxUseCasesTest {
    private val repos = FakeRepositories()
    private val ids = FakeIdGenerator()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 14, 20), TimeZone.of("America/Sao_Paulo"))
    private val capture = CaptureInboxItemUseCase(repos.inbox, ids, time)
    private val convert = ConvertInboxItemUseCase(repos.inbox, ids, time, repos.settings)

    @Test
    fun `BR-INBOX-001 capture needs only non-blank text`() = runTest {
        val item = capture("  Change the motorcycle oil  ").assertSuccess()

        assertEquals("Change the motorcycle oil", item.text)
        assertEquals(item, repos.inbox.get(item.id))
        capture("   ").assertValidation("BR-INBOX-001")
        capture("x".repeat(TextLimits.INBOX_TEXT + 1)).assertValidation("BR-GEN-002")
    }

    @Test
    fun `BR-GEN-003 BR-GEN-004 new items get a fresh id and timestamps`() = runTest {
        val first = capture("One").assertSuccess()
        time.advanceBy(5.minutes)
        val second = capture("Two").assertSuccess()

        assertEquals("00000000-0000-0000-0000-000000000001", first.id.value)
        assertEquals(first.createdAt, first.updatedAt)
        assertEquals(time.now(), second.createdAt)
    }

    @Test
    fun `BR-INBOX-003 inbox is observed newest first with its count`() = runTest {
        capture("Old").assertSuccess()
        time.advanceBy(1.minutes)
        capture("New").assertSuccess()

        assertEquals(listOf("New", "Old"), ObserveInboxUseCase(repos.inbox)().first().map { it.text })
        assertEquals(2, ObserveInboxCountUseCase(repos.inbox)().first())
    }

    @Test
    fun `BR-INBOX-006 editing changes only the text and discarding can be undone`() = runTest {
        val item = capture("Buy bread").assertSuccess()
        time.advanceBy(1.minutes)

        val edited = EditInboxItemUseCase(repos.inbox, time)(item.id, "Buy bread and milk").assertSuccess()
        assertEquals(item.createdAt, edited.createdAt)
        assertEquals(time.now(), edited.updatedAt)
        EditInboxItemUseCase(repos.inbox, time)(item.id, " ").assertValidation("BR-INBOX-001")
        EditInboxItemUseCase(repos.inbox, time)(InboxItemId("missing"), "x").assertNotFound()

        val discarded = DiscardInboxItemUseCase(repos.inbox)(item.id).assertSuccess()
        assertNull(repos.inbox.get(item.id))
        DiscardInboxItemUseCase(repos.inbox)(item.id).assertNotFound()
        RestoreInboxItemUseCase(repos.inbox)(discarded).assertSuccess()
        assertEquals(edited, repos.inbox.get(item.id))
    }

    @Test
    fun `BR-INBOX-004 converting to a task splits title and notes and removes the item`() = runTest {
        val item = capture("Renew passport\nTake old passport\nand photos").assertSuccess()

        val target = assertIs<ConversionTarget.ToTask>(convert(item.id, ConversionType.Task).assertSuccess())

        assertEquals("Renew passport", target.task.title)
        assertEquals("Take old passport\nand photos", target.task.notes)
        assertEquals(target.task, repos.tasks.get(target.task.id))
        assertNull(repos.inbox.get(item.id))
        convert(item.id, ConversionType.Task).assertNotFound()
    }

    @Test
    fun `BR-INBOX-004 titles are truncated to the title limit`() = runTest {
        val item = capture("t".repeat(TextLimits.TITLE + 20)).assertSuccess()

        val target = assertIs<ConversionTarget.ToTask>(convert(item.id, ConversionType.Task).assertSuccess())

        assertEquals(TextLimits.TITLE, target.task.title.length)
        assertNull(target.task.notes)
    }

    @Test
    fun `BR-INBOX-004 BR-APPT-001 converted appointments start at the next full hour in the device zone`() = runTest {
        val item = capture("Dentist").assertSuccess()

        val target =
            assertIs<ConversionTarget.ToAppointment>(convert(item.id, ConversionType.Appointment).assertSuccess())

        val timing = assertIs<AppointmentTiming.Timed>(target.appointment.timing)
        assertEquals(LocalDateTime(2026, 1, 15, 15, 0), timing.start)
        assertEquals(null, timing.end)
        assertEquals(TimeZone.of("America/Sao_Paulo"), timing.timeZone)
    }

    @Test
    fun `BR-APPT-001 the next full hour rolls over midnight`() = runTest {
        time.setLocal(LocalDateTime(2026, 1, 15, 23, 40))
        val item = capture("Late call").assertSuccess()

        val target =
            assertIs<ConversionTarget.ToAppointment>(convert(item.id, ConversionType.Appointment).assertSuccess())

        assertEquals(LocalDateTime(2026, 1, 16, 0, 0), (target.appointment.timing as AppointmentTiming.Timed).start)
    }

    @Test
    fun `BR-INBOX-004 BR-MATTER-001 converted matters start with a Created entry`() = runTest {
        val item = capture("Car documents\nNeed the receipt").assertSuccess()

        val target = assertIs<ConversionTarget.ToMatter>(convert(item.id, ConversionType.Matter).assertSuccess())

        assertEquals("Car documents", target.matter.title)
        assertEquals("Need the receipt", target.matter.description)
        assertEquals(MatterEntryKind.Created, target.created.kind)
        assertEquals(target.matter, repos.matters.get(target.matter.id))
        repos.matters.observeHistory(target.matter.id).test { assertEquals(listOf(target.created), awaitItem()) }
    }
}
