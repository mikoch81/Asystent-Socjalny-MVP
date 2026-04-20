package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

/**
 * Pure-JVM tests for [CaseExporter] readme builder.
 * JSON-roundtrip would require Android runtime (org.json).
 */
class CaseExporterReadmeTest {

    private val sampleCase = CaseRecord(
        caseId = "case-1",
        scenarioId = "sc-1",
        scenarioTitle = "Przemoc w rodzinie",
        status = CaseStatus.IN_PROGRESS,
        riskLevel = RiskLevel.HIGH,
        updatedAt = 1_700_000_000_000L,
        isDraft = false,
        locationPreview = "ul. Piłsudskiego 12",
        lifecycle = CaseLifecycle.ACTIVE,
        hasNote = true
    )

    private fun note(id: String) = CaseDocument(
        documentId = id,
        caseId = "case-1",
        type = DocumentType.NOTE_DRAFT,
        title = "Notatka $id",
        fileName = "$id.txt",
        textContent = "treść",
        filePath = "",
        createdAt = 0L
    )

    private fun pdf(id: String) = CaseDocument(
        documentId = id,
        caseId = "case-1",
        type = DocumentType.PDF_DRAFT,
        title = "PDF $id",
        fileName = "$id.pdf",
        textContent = "",
        filePath = "/tmp/$id.pdf",
        createdAt = 0L
    )

    @Test
    fun buildReadme_lists_metadata_and_counts() {
        val readme = CaseExporter.buildReadme(sampleCase, listOf(note("a"), pdf("b")))
        assertTrue(readme.contains("Przemoc w rodzinie"))
        assertTrue(readme.contains("case-1"))
        assertTrue(readme.contains("ul. Piłsudskiego 12"))
        assertTrue(readme.contains("notatki tekstowe (1)"))
        assertTrue(readme.contains("wygenerowane PDF (1"))
        assertTrue(readme.contains("RODO"))
    }

    @Test
    fun buildReadme_handlesEmptyDocs() {
        val readme = CaseExporter.buildReadme(sampleCase, emptyList())
        assertTrue(readme.contains("notatki tekstowe (0)"))
        assertTrue(readme.contains("wygenerowane PDF (0"))
    }

    @Test
    fun buildReadme_skipsLocation_whenBlank() {
        val noLoc = sampleCase.copy(locationPreview = "")
        val readme = CaseExporter.buildReadme(noLoc, emptyList())
        assertTrue(!readme.contains("Miejsce:"))
    }
}
