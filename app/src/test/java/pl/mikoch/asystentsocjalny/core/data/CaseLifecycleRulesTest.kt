package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

class CaseLifecycleRulesTest {

    private fun record(
        lifecycle: CaseLifecycle = CaseLifecycle.ACTIVE,
        status: CaseStatus = CaseStatus.IN_PROGRESS
    ) = CaseRecord(
        caseId = "c1",
        scenarioId = "s1",
        scenarioTitle = "Test",
        status = status,
        riskLevel = RiskLevel.MEDIUM,
        updatedAt = 100L,
        lifecycle = lifecycle
    )

    // --- canClose ---

    @Test
    fun `canClose returns true for ACTIVE`() {
        assertTrue(CaseLifecycleRules.canClose(record(CaseLifecycle.ACTIVE)))
    }

    @Test
    fun `canClose returns true for READY_TO_CLOSE`() {
        assertTrue(CaseLifecycleRules.canClose(record(CaseLifecycle.READY_TO_CLOSE)))
    }

    @Test
    fun `canClose returns false for CLOSED`() {
        assertFalse(CaseLifecycleRules.canClose(record(CaseLifecycle.CLOSED)))
    }

    @Test
    fun `canClose returns false for ARCHIVED`() {
        assertFalse(CaseLifecycleRules.canClose(record(CaseLifecycle.ARCHIVED)))
    }

    // --- canArchive ---

    @Test
    fun `canArchive returns true only for CLOSED`() {
        assertTrue(CaseLifecycleRules.canArchive(record(CaseLifecycle.CLOSED)))
        assertFalse(CaseLifecycleRules.canArchive(record(CaseLifecycle.ACTIVE)))
        assertFalse(CaseLifecycleRules.canArchive(record(CaseLifecycle.READY_TO_CLOSE)))
        assertFalse(CaseLifecycleRules.canArchive(record(CaseLifecycle.ARCHIVED)))
    }

    // --- canRestore ---

    @Test
    fun `canRestore returns true for CLOSED and ARCHIVED`() {
        assertTrue(CaseLifecycleRules.canRestore(record(CaseLifecycle.CLOSED)))
        assertTrue(CaseLifecycleRules.canRestore(record(CaseLifecycle.ARCHIVED)))
        assertFalse(CaseLifecycleRules.canRestore(record(CaseLifecycle.ACTIVE)))
        assertFalse(CaseLifecycleRules.canRestore(record(CaseLifecycle.READY_TO_CLOSE)))
    }

    // --- canEdit ---

    @Test
    fun `canEdit returns true for ACTIVE and READY_TO_CLOSE`() {
        assertTrue(CaseLifecycleRules.canEdit(record(CaseLifecycle.ACTIVE)))
        assertTrue(CaseLifecycleRules.canEdit(record(CaseLifecycle.READY_TO_CLOSE)))
        assertFalse(CaseLifecycleRules.canEdit(record(CaseLifecycle.CLOSED)))
        assertFalse(CaseLifecycleRules.canEdit(record(CaseLifecycle.ARCHIVED)))
    }

    // --- autoLifecycle ---

    @Test
    fun `autoLifecycle returns READY_TO_CLOSE when status is READY_TO_CLOSE`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.READY_TO_CLOSE)
        assertEquals(CaseLifecycle.READY_TO_CLOSE, CaseLifecycleRules.autoLifecycle(r))
    }

    @Test
    fun `autoLifecycle returns ACTIVE when status is IN_PROGRESS`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.IN_PROGRESS)
        assertEquals(CaseLifecycle.ACTIVE, CaseLifecycleRules.autoLifecycle(r))
    }

    @Test
    fun `autoLifecycle returns ACTIVE when status is DRAFT`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.DRAFT)
        assertEquals(CaseLifecycle.ACTIVE, CaseLifecycleRules.autoLifecycle(r))
    }

    @Test
    fun `autoLifecycle preserves CLOSED even if status changes`() {
        val r = record(CaseLifecycle.CLOSED, CaseStatus.IN_PROGRESS)
        assertEquals(CaseLifecycle.CLOSED, CaseLifecycleRules.autoLifecycle(r))
    }

    @Test
    fun `autoLifecycle preserves ARCHIVED even if status changes`() {
        val r = record(CaseLifecycle.ARCHIVED, CaseStatus.READY_TO_CLOSE)
        assertEquals(CaseLifecycle.ARCHIVED, CaseLifecycleRules.autoLifecycle(r))
    }

    // --- CaseLifecycle labels ---

    @Test
    fun `CaseLifecycle entries count is 4`() {
        assertEquals(4, CaseLifecycle.entries.size)
    }

    @Test
    fun `CaseLifecycle labels are Polish`() {
        assertEquals("Aktywna", CaseLifecycle.ACTIVE.label)
        assertEquals("Do zamknięcia", CaseLifecycle.READY_TO_CLOSE.label)
        assertEquals("Zamknięta", CaseLifecycle.CLOSED.label)
        assertEquals("Zarchiwizowana", CaseLifecycle.ARCHIVED.label)
    }
}
