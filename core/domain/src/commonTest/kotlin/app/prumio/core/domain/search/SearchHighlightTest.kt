package app.prumio.core.domain.search

import kotlin.test.Test
import kotlin.test.assertEquals

class SearchHighlightTest {
    @Test
    fun `BR-SEARCH-002 highlights ignore case and accents`() {
        assertEquals(listOf(0..11), highlightRanges("Documentação do carro", "documentacao"))
        assertEquals(listOf(4..6), highlightRanges("Pay RENT", "ren"))
    }

    @Test
    fun `every word is highlighted and touching matches are merged`() {
        assertEquals(listOf(0..2, 4..6), highlightRanges("Car insurance papers", "car ins"))
        assertEquals(listOf(0..3), highlightRanges("abcd", "ab cd"))
    }

    @Test
    fun `nothing is highlighted without a match or a query`() {
        assertEquals(emptyList(), highlightRanges("Groceries", "bank"))
        assertEquals(emptyList(), highlightRanges("Groceries", "  "))
    }

    @Test
    fun `ranges point at the original text when folding changes its length`() {
        assertEquals(listOf(0..4), highlightRanges("Straße 5", "strass"))
    }
}
