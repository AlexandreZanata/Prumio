package app.prumio.core.domain.search

import app.prumio.core.model.Appointment
import app.prumio.core.model.InboxItem
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.Task

private val Diacritics: Map<Char, String> = buildMap {
    "àáâãäåāăą".forEach { put(it, "a") }
    "çćĉċč".forEach { put(it, "c") }
    "ďđ".forEach { put(it, "d") }
    "èéêëēĕėęě".forEach { put(it, "e") }
    "ĝğġģ".forEach { put(it, "g") }
    "ĥħ".forEach { put(it, "h") }
    "ìíîïĩīĭįı".forEach { put(it, "i") }
    "ĵ".forEach { put(it, "j") }
    "ķ".forEach { put(it, "k") }
    "ĺļľŀł".forEach { put(it, "l") }
    "ñńņňŉ".forEach { put(it, "n") }
    "òóôõöøōŏő".forEach { put(it, "o") }
    "ŕŗř".forEach { put(it, "r") }
    "śŝşš".forEach { put(it, "s") }
    "ţťŧ".forEach { put(it, "t") }
    "ùúûüũūŭůűų".forEach { put(it, "u") }
    "ŵ".forEach { put(it, "w") }
    "ýÿŷ".forEach { put(it, "y") }
    "źżž".forEach { put(it, "z") }
    put('æ', "ae")
    put('œ', "oe")
    put('ß', "ss")
}

private val Whitespace = Regex("""\s+""")

/**
 * Lowercases, removes diacritics of Latin letters and collapses whitespace, so that
 * "Documentação" and "documentacao" match (BR-SEARCH-002). Pure Kotlin: works on every platform.
 */
fun normalizeForSearch(text: String): String = buildString(text.length) {
    text.lowercase().forEach { char -> append(Diacritics[char] ?: char) }
}.replace(Whitespace, " ").trim()

/** The distinct normalized words of a query. */
fun searchTerms(query: String): List<String> =
    normalizeForSearch(query).split(' ').filter { it.isNotEmpty() }.distinct()

/** What is searchable in each item (BR-SEARCH-001). Stored normalized by the data layer. */
fun Task.searchableText(): String = normalizeForSearch(listOfNotNull(title, notes).joinToString(" "))

fun Appointment.searchableText(): String = normalizeForSearch(listOfNotNull(title, notes, location).joinToString(" "))

fun Matter.searchableText(): String =
    normalizeForSearch(listOfNotNull(title, description, waitingFor).joinToString(" "))

fun InboxItem.searchableText(): String = normalizeForSearch(text)

/** Only notes are searchable among history entries. */
fun MatterEntry.searchableText(): String = if (kind == MatterEntryKind.Note) normalizeForSearch(text.orEmpty()) else ""
