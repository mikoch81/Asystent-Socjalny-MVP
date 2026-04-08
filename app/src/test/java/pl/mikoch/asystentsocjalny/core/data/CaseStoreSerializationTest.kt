package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

/**
 * Tests for [CaseRecord] and [CaseStatus] domain model logic.
 * Serialization round-trip tests require Android runtime (org.json)
 * and are covered by the pre-existing DraftSerializationTest pattern.
 */
class CaseStoreSerializationTest {

    @Test
    fun `CaseStatus label values are Polish`() {
        assertEquals("Szkic", CaseStatus.DRAFT.label)
        assertEquals("W toku", CaseStatus.IN_PROGRESS.label)
        assertEquals("Gotowe do zamknięcia", CaseStatus.READY_TO_CLOSE.label)
    }

    @Test
    fun `CaseStatus entries count is 3`() {
        assertEquals(3, CaseStatus.entries.size)
    }

    @Test
    fun `RiskLevel entries count is 3`() {
        assertEquals(3, RiskLevel.entries.size)
    }

    @Test
    fun `CaseRecord default isDraft is true`() {
        val record = CaseRecord(
            caseId = "c1",
            scenarioId = "s1",
            scenarioTitle = "T",
            status = CaseStatus.DRAFT,
            riskLevel = RiskLevel.LOW,
            updatedAt = 0L
        )
        assertEquals(true, record.isDraft)
        assertEquals("", record.locationPreview)
        assertEquals(CaseLifecycle.ACTIVE, record.lifecycle)
        assertEquals(false, record.hasNote)
    }

    @Test
    fun `CaseRecord copy changes status`() {
        val record = CaseRecord(
            caseId = "c1",
            scenarioId = "s1",
            scenarioTitle = "Test",
            status = CaseStatus.DRAFT,
            riskLevel = RiskLevel.LOW,
            updatedAt = 100L
        )
        val updated = record.copy(status = CaseStatus.IN_PROGRESS, riskLevel = RiskLevel.HIGH)
        assertEquals(CaseStatus.IN_PROGRESS, updated.status)
        assertEquals(RiskLevel.HIGH, updated.riskLevel)
        assertEquals("c1", updated.caseId)
    }

    @Test
    fun `CaseRecord equality`() {
        val a = CaseRecord("id", "s", "T", CaseStatus.DRAFT, RiskLevel.LOW, 1L)
        val b = CaseRecord("id", "s", "T", CaseStatus.DRAFT, RiskLevel.LOW, 1L)
        assertEquals(a, b)
    }

    @Test
    fun `CaseStatus valueOf round-trips`() {
        for (status in CaseStatus.entries) {
            assertEquals(status, CaseStatus.valueOf(status.name))
        }
    }

    @Test
    fun `RiskLevel valueOf round-trips`() {
        for (level in RiskLevel.entries) {
            assertEquals(level, RiskLevel.valueOf(level.name))
        }
    }

    @Test
    fun `CaseLifecycle valueOf round-trips`() {
        for (lc in CaseLifecycle.entries) {
            assertEquals(lc, CaseLifecycle.valueOf(lc.name))
        }
    }

    @Test
    fun `CaseRecord copy changes lifecycle`() {
        val record = CaseRecord(
            caseId = "c1",
            scenarioId = "s1",
            scenarioTitle = "Test",
            status = CaseStatus.DRAFT,
            riskLevel = RiskLevel.LOW,
            updatedAt = 100L
        )
        val closed = record.copy(lifecycle = CaseLifecycle.CLOSED)
        assertEquals(CaseLifecycle.CLOSED, closed.lifecycle)
        assertEquals(CaseLifecycle.ACTIVE, record.lifecycle)
    }
}
