package app.prumio.core.domain.search

import app.prumio.core.model.MatterEntryKind
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchTextTest {
    @Test
    fun `BR-SEARCH-002 normalization ignores case, accents and extra spaces`() {
        assertEquals("documentacao do veiculo", normalizeForSearch("  Documentação   do VEÍCULO "))
        assertEquals("acao pendencia aguardando", normalizeForSearch("Ação Pendência Aguardando"))
        assertEquals("strasse oeuvre aeon", normalizeForSearch("Straße Œuvre Æon"))
    }

    @Test
    fun `BR-SEARCH-002 queries become distinct normalized words`() {
        assertEquals(listOf("carro", "documento"), searchTerms("Carro  documento carro"))
        assertEquals(emptyList(), searchTerms("   "))
    }

    @Test
    fun `BR-SEARCH-001 searchable text covers the right fields`() {
        assertEquals(
            "pagar internet conta da casa",
            aTask(title = "Pagar internet", notes = "Conta da casa").searchableText(),
        )
        assertEquals("medico clinica", anAppointment(title = "Médico", location = "Clínica").searchableText())
        assertEquals(
            "documentos despachante",
            aMatter(title = "Documentos", waitingFor = "Despachante").searchableText(),
        )
        assertEquals("trocar oleo", anInboxItem(text = "Trocar óleo").searchableText())
        assertEquals("liguei", aMatterEntry(text = "Liguei").searchableText())
        assertEquals("", aMatterEntry(kind = MatterEntryKind.StatusChanged, text = "x").searchableText())
    }
}
