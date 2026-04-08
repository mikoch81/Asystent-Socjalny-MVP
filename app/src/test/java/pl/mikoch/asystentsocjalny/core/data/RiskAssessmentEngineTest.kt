package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

class RiskAssessmentEngineTest {

    // --- HIGH risk ---

    @Test
    fun high_whenAnyCriticalUnchecked() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 5,
            completedSteps = 4,
            uncheckedCriticalCount = 1
        )
        assertEquals(RiskLevel.HIGH, result.level)
        assertEquals(1, result.reasons.size)
        assert(result.reasons[0].contains("krytyczne"))
    }

    @Test
    fun high_whenMultipleCriticalUnchecked() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 6,
            completedSteps = 0,
            uncheckedCriticalCount = 3
        )
        assertEquals(RiskLevel.HIGH, result.level)
        assert(result.reasons.any { it.contains("3") })
    }

    @Test
    fun high_priorityOverMedium() {
        // Even with low progress, critical steps dominate
        val result = RiskAssessmentEngine.assess(
            totalSteps = 10,
            completedSteps = 1,
            uncheckedCriticalCount = 2
        )
        assertEquals(RiskLevel.HIGH, result.level)
        // Both reasons present: critical + progress
        assertEquals(2, result.reasons.size)
    }

    // --- MEDIUM risk ---

    @Test
    fun medium_whenProgressBelowHalf() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 6,
            completedSteps = 2,
            uncheckedCriticalCount = 0
        )
        assertEquals(RiskLevel.MEDIUM, result.level)
        assert(result.reasons.any { it.contains("50%") })
    }

    @Test
    fun medium_whenZeroCompleted() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 4,
            completedSteps = 0,
            uncheckedCriticalCount = 0
        )
        assertEquals(RiskLevel.MEDIUM, result.level)
    }

    @Test
    fun medium_whenPartialProgressNotHalf() {
        // 3 of 7 = 42%, still below 50%
        val result = RiskAssessmentEngine.assess(
            totalSteps = 7,
            completedSteps = 3,
            uncheckedCriticalCount = 0
        )
        assertEquals(RiskLevel.MEDIUM, result.level)
    }

    @Test
    fun medium_whenExactlyHalf() {
        // 3 of 6 = 50%, not below — integer division: 6/2=3, 3 < 3 is false
        val result = RiskAssessmentEngine.assess(
            totalSteps = 6,
            completedSteps = 3,
            uncheckedCriticalCount = 0
        )
        // 50% is not below 50%, so falls to else branch (partial progress)
        assertEquals(RiskLevel.MEDIUM, result.level)
    }

    // --- LOW risk ---

    @Test
    fun low_whenAllCompleted() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 5,
            completedSteps = 5,
            uncheckedCriticalCount = 0
        )
        assertEquals(RiskLevel.LOW, result.level)
        assert(result.reasons.any { it.contains("Wszystkie") })
    }

    // --- Edge cases ---

    @Test
    fun medium_whenZeroTotalSteps() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 0,
            completedSteps = 0,
            uncheckedCriticalCount = 0
        )
        assertEquals(RiskLevel.MEDIUM, result.level)
    }

    @Test
    fun reasonsContainCriticalCountNumber() {
        val result = RiskAssessmentEngine.assess(
            totalSteps = 3,
            completedSteps = 2,
            uncheckedCriticalCount = 1
        )
        assert(result.reasons.any { "1" in it })
    }
}
