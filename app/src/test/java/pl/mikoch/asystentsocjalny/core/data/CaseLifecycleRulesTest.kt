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
        status: CaseStatus = CaseStatus.IN_PROGRESS,
        hasNote: Boolean = false
    ) = CaseRecord(
        caseId = "c1",
        scenarioId = "s1",
        scenarioTitle = "Test",
        status = status,
        riskLevel = RiskLevel.MEDIUM,
        updatedAt = 100L,
        lifecycle = lifecycle,
        hasNote = hasNote
    )

    // --- canClose (stored lifecycle only: ACTIVE can close) ---

    @Test
    fun `canClose returns true for ACTIVE`() {
        assertTrue(CaseLifecycleRules.canClose(record(CaseLifecycle.ACTIVE)))
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
        assertFalse(CaseLifecycleRules.canArchive(record(CaseLifecycle.ARCHIVED)))
    }

    // --- canRestore ---

    @Test
    fun `canRestore returns true for CLOSED and ARCHIVED`() {
        assertTrue(CaseLifecycleRules.canRestore(record(CaseLifecycle.CLOSED)))
        assertTrue(CaseLifecycleRules.canRestore(record(CaseLifecycle.ARCHIVED)))
        assertFalse(CaseLifecycleRules.canRestore(record(CaseLifecycle.ACTIVE)))
    }

    // --- canEdit (stored lifecycle only: ACTIVE can edit) ---

    @Test
    fun `canEdit returns true for ACTIVE`() {
        assertTrue(CaseLifecycleRules.canEdit(record(CaseLifecycle.ACTIVE)))
        assertFalse(CaseLifecycleRules.canEdit(record(CaseLifecycle.CLOSED)))
        assertFalse(CaseLifecycleRules.canEdit(record(CaseLifecycle.ARCHIVED)))
    }

    // --- displayLifecycle (dynamically computed) ---

    @Test
    fun `displayLifecycle returns READY_TO_CLOSE when status READY_TO_CLOSE and hasNote`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.READY_TO_CLOSE, hasNote = true)
        assertEquals(CaseLifecycle.READY_TO_CLOSE, CaseLifecycleRules.displayLifecycle(r))
    }

    @Test
    fun `displayLifecycle returns ACTIVE when status READY_TO_CLOSE but no note`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.READY_TO_CLOSE, hasNote = false)
        assertEquals(CaseLifecycle.ACTIVE, CaseLifecycleRules.displayLifecycle(r))
    }

    @Test
    fun `displayLifecycle returns ACTIVE when status IN_PROGRESS even with note`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.IN_PROGRESS, hasNote = true)
        assertEquals(CaseLifecycle.ACTIVE, CaseLifecycleRules.displayLifecycle(r))
    }

    @Test
    fun `displayLifecycle returns ACTIVE when status DRAFT`() {
        val r = record(CaseLifecycle.ACTIVE, CaseStatus.DRAFT)
        assertEquals(CaseLifecycle.ACTIVE, CaseLifecycleRules.displayLifecycle(r))
    }

    @Test
    fun `displayLifecycle preserves CLOSED`() {
        val r = record(CaseLifecycle.CLOSED, CaseStatus.READY_TO_CLOSE, hasNote = true)
        assertEquals(CaseLifecycle.CLOSED, CaseLifecycleRules.displayLifecycle(r))
    }

    @Test
    fun `displayLifecycle preserves ARCHIVED`() {
        val r = record(CaseLifecycle.ARCHIVED, CaseStatus.READY_TO_CLOSE, hasNote = true)
        assertEquals(CaseLifecycle.ARCHIVED, CaseLifecycleRules.displayLifecycle(r))
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
