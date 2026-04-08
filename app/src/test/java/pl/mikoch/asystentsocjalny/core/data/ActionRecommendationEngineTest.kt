package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.RecommendationPriority
import pl.mikoch.asystentsocjalny.core.model.RiskAssessment
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi
import pl.mikoch.asystentsocjalny.features.urgent.model.GuidanceUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus

class ActionRecommendationEngineTest {

    // --- helpers ---

    private fun step(id: String, critical: Boolean = false) =
        ChecklistStepUi(id = id, text = "Krok $id", isCritical = critical)

    private fun progress(
        total: Int,
        completed: Int,
        uncheckedCritical: List<ChecklistStepUi> = emptyList(),
        status: UrgentStatus = UrgentStatus.IN_PROGRESS
    ) = UrgentProgress(
        totalSteps = total,
        completedSteps = completed,
        progressText = "Wykonano $completed z $total kroków",
        status = status,
        uncheckedCriticalSteps = uncheckedCritical
    )

    private fun risk(level: RiskLevel) = RiskAssessment(level = level, reasons = emptyList())

    private fun guidance(
        escalationRequired: Boolean = false,
        escalationNote: String = "",
        notify: List<String> = emptyList(),
        documents: List<String> = emptyList(),
        doNotMiss: List<String> = emptyList()
    ) = GuidanceUi(
        notify = notify,
        documents = documents,
        doNotMiss = doNotMiss,
        escalationRequired = escalationRequired,
        escalationNote = escalationNote
    )

    // --- HIGH priority ---

    @Test
    fun `HIGH when unchecked critical steps exist`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertEquals(RecommendationPriority.HIGH, result.priority)
    }

    @Test
    fun `HIGH title is action-oriented`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertEquals("Wymagane pilne działanie", result.title)
    }

    @Test
    fun `HIGH actions include critical steps text`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertTrue(result.actions.any { it.contains("Krok c1") })
    }

    @Test
    fun `HIGH warns about not closing case`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertTrue(result.actions.any { it.contains("Nie zamykaj") })
    }

    @Test
    fun `HIGH includes escalation when guidance requires it`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = guidance(escalationRequired = true, escalationNote = "Powiadom przełożonego")
        )
        assertTrue(result.actions.any { it.contains("Eskaluj") })
        assertTrue(result.warnings.any { it.contains("Powiadom przełożonego") })
    }

    @Test
    fun `HIGH summary mentions unchecked critical count`() {
        val steps = listOf(step("c1", true), step("c2", true))
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 1, uncheckedCritical = steps),
            guidance = null
        )
        assertTrue(result.summary.contains("2"))
    }

    @Test
    fun `HIGH warnings always include podopieczny risk`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertTrue(result.warnings.any { it.contains("podopiecznego") })
    }

    // --- MEDIUM priority ---

    @Test
    fun `MEDIUM when no critical issues but incomplete`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 3, status = UrgentStatus.IN_PROGRESS),
            guidance = null
        )
        assertEquals(RecommendationPriority.MEDIUM, result.priority)
    }

    @Test
    fun `MEDIUM title encourages continuation`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 3),
            guidance = null
        )
        assertEquals("Kontynuuj realizację", result.title)
    }

    @Test
    fun `MEDIUM actions mention remaining steps count`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 3),
            guidance = null
        )
        assertTrue(result.actions.any { it.contains("7") })
    }

    @Test
    fun `MEDIUM includes documents from guidance`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 6),
            guidance = guidance(documents = listOf("Niebieska Karta"))
        )
        assertTrue(result.actions.any { it.contains("Niebieska Karta") })
    }

    @Test
    fun `MEDIUM includes notify from guidance`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 6),
            guidance = guidance(notify = listOf("Policja", "Kurator"))
        )
        assertTrue(result.actions.any { it.contains("Policja") && it.contains("Kurator") })
    }

    @Test
    fun `MEDIUM warns about low progress when below half`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 2),
            guidance = null
        )
        assertTrue(result.warnings.any { it.contains("poniżej połowy") })
    }

    @Test
    fun `MEDIUM no low-progress warning when above half`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(10, 7),
            guidance = null
        )
        assertTrue(result.warnings.none { it.contains("poniżej połowy") })
    }

    // --- LOW priority ---

    @Test
    fun `LOW when all steps completed`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(5, 5, status = UrgentStatus.READY_TO_CLOSE),
            guidance = null
        )
        assertEquals(RecommendationPriority.LOW, result.priority)
    }

    @Test
    fun `LOW title indicates closure readiness`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(5, 5, status = UrgentStatus.READY_TO_CLOSE),
            guidance = null
        )
        assertEquals("Sprawa do zamknięcia", result.title)
    }

    @Test
    fun `LOW actions include note generation and review`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(5, 5, status = UrgentStatus.READY_TO_CLOSE),
            guidance = null
        )
        assertTrue(result.actions.any { it.contains("notatkę") })
        assertTrue(result.actions.any { it.contains("zamknięcia") })
    }

    @Test
    fun `LOW warns about escalation if guidance required it`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(5, 5, status = UrgentStatus.READY_TO_CLOSE),
            guidance = guidance(escalationRequired = true)
        )
        assertTrue(result.warnings.any { it.contains("eskalacja") })
    }

    @Test
    fun `LOW no warnings when no escalation needed`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(5, 5, status = UrgentStatus.READY_TO_CLOSE),
            guidance = null
        )
        assertTrue(result.warnings.isEmpty())
    }

    // --- edge cases ---

    @Test
    fun `null guidance does not crash for HIGH`() {
        val criticalStep = step("c1", critical = true)
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.HIGH),
            progress = progress(5, 2, uncheckedCritical = listOf(criticalStep)),
            guidance = null
        )
        assertEquals(RecommendationPriority.HIGH, result.priority)
        assertTrue(result.actions.isNotEmpty())
    }

    @Test
    fun `null guidance does not crash for MEDIUM`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.MEDIUM),
            progress = progress(5, 2),
            guidance = null
        )
        assertEquals(RecommendationPriority.MEDIUM, result.priority)
    }

    @Test
    fun `zero total steps yields LOW`() {
        val result = ActionRecommendationEngine.recommend(
            riskAssessment = risk(RiskLevel.LOW),
            progress = progress(0, 0),
            guidance = null
        )
        assertEquals(RecommendationPriority.LOW, result.priority)
    }

    @Test
    fun `actions list is never empty`() {
        val scenarios = listOf(
            ActionRecommendationEngine.recommend(
                risk(RiskLevel.HIGH),
                progress(5, 0, listOf(step("c1", true))),
                null
            ),
            ActionRecommendationEngine.recommend(
                risk(RiskLevel.MEDIUM),
                progress(5, 2),
                null
            ),
            ActionRecommendationEngine.recommend(
                risk(RiskLevel.LOW),
                progress(5, 5),
                null
            )
        )
        scenarios.forEach { assertTrue(it.actions.isNotEmpty()) }
    }
}
