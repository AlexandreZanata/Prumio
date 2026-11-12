package app.prumio.core.domain.search

/**
 * Where the words of [query] appear in [text], as index ranges of the original [text], matched the
 * same way search matches (case- and accent-insensitive, BR-SEARCH-002). Overlapping ranges are merged.
 */
fun highlightRanges(text: String, query: String): List<IntRange> {
    val terms = searchTerms(query)
    if (terms.isEmpty()) return emptyList()
    // Normalize character by character, remembering which original index each normalized char came from.
    val normalized = StringBuilder()
    val origin = ArrayList<Int>(text.length)
    text.forEachIndexed { index, char ->
        char.lowercase().forEach { lower ->
            val folded = normalizeForSearch(lower.toString()).ifEmpty { " " }
            folded.forEach {
                normalized.append(it)
                origin += index
            }
        }
    }
    val haystack = normalized.toString()
    val ranges = terms.flatMap { term ->
        generateSequence(haystack.indexOf(term)) { start -> haystack.indexOf(term, start + 1).takeIf { it >= 0 } }
            .takeWhile { it >= 0 }
            .map { start -> origin[start]..origin[start + term.length - 1] }
            .toList()
    }
    return ranges.sortedBy { it.first }.fold(mutableListOf()) { merged, range ->
        val last = merged.lastOrNull()
        if (last != null && range.first <= last.last + 1) {
            merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
        } else {
            merged += range
        }
        merged
    }
}
