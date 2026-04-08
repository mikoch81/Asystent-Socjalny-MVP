package pl.mikoch.asystentsocjalny.features.urgent.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UrgentProgressCalculatorTest {

    private fun step(id: String, critical: Boolean = false) =
        ChecklistStepUi(id = id, text = "Step $id", isCritical = critical)

    // --- Progress text ---

    @Test
    fun progressText_zeroSteps() {
        val result = UrgentProgressCalculator.calculate(emptyList(), emptyList())
        assertEquals("Wykonano 0 z 0 kroków", result.progressText)
    }

    @Test
    fun progressText_noneCompleted() {
        val steps = listOf(step("1"), step("2"), step("3"))
        val checked = listOf(false, false, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals("Wykonano 0 z 3 kroków", result.progressText)
        assertEquals(3, result.totalSteps)
        assertEquals(0, result.completedSteps)
    }

    @Test
    fun progressText_someCompleted() {
        val steps = listOf(step("1"), step("2"), step("3"))
        val checked = listOf(true, false, true)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals("Wykonano 2 z 3 kroków", result.progressText)
        assertEquals(2, result.completedSteps)
    }

    @Test
    fun progressText_allCompleted() {
        val steps = listOf(step("1"), step("2"))
        val checked = listOf(true, true)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals("Wykonano 2 z 2 kroków", result.progressText)
    }

    // --- Status: READY_TO_CLOSE ---

    @Test
    fun status_readyToClose_whenAllChecked() {
        val steps = listOf(step("1"), step("2", critical = true))
        val checked = listOf(true, true)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(UrgentStatus.READY_TO_CLOSE, result.status)
    }

    @Test
    fun status_readyToClose_emptySteps() {
        val result = UrgentProgressCalculator.calculate(emptyList(), emptyList())
        assertEquals(UrgentStatus.IN_PROGRESS, result.status)
    }

    // --- Status: NEEDS_ATTENTION ---

    @Test
    fun status_needsAttention_whenCriticalUnchecked() {
        val steps = listOf(step("1"), step("2", critical = true))
        val checked = listOf(true, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(UrgentStatus.NEEDS_ATTENTION, result.status)
    }

    @Test
    fun status_needsAttention_whenAllUncheckedWithCritical() {
        val steps = listOf(step("1"), step("2", critical = true))
        val checked = listOf(false, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(UrgentStatus.NEEDS_ATTENTION, result.status)
    }

    // --- Status: IN_PROGRESS ---

    @Test
    fun status_inProgress_whenOnlyRegularStepsRemain() {
        val steps = listOf(step("1"), step("2"), step("3", critical = true))
        val checked = listOf(false, false, true)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(UrgentStatus.IN_PROGRESS, result.status)
    }

    @Test
    fun status_inProgress_noCriticalStepsAllUnchecked() {
        val steps = listOf(step("1"), step("2"))
        val checked = listOf(false, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(UrgentStatus.IN_PROGRESS, result.status)
    }

    // --- Unchecked critical steps ---

    @Test
    fun uncheckedCritical_returnsOnlyUncheckedCriticalSteps() {
        val critical1 = step("c1", critical = true)
        val critical2 = step("c2", critical = true)
        val regular = step("r1")
        val steps = listOf(regular, critical1, critical2)
        val checked = listOf(false, true, false)

        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(1, result.uncheckedCriticalSteps.size)
        assertEquals("c2", result.uncheckedCriticalSteps[0].id)
    }

    @Test
    fun uncheckedCritical_emptyWhenAllCriticalChecked() {
        val steps = listOf(step("1", critical = true), step("2"))
        val checked = listOf(true, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(0, result.uncheckedCriticalSteps.size)
    }

    @Test
    fun uncheckedCritical_emptyWhenNoCriticalSteps() {
        val steps = listOf(step("1"), step("2"))
        val checked = listOf(false, false)
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(0, result.uncheckedCriticalSteps.size)
    }

    // --- Edge: checkedStates shorter than steps ---

    @Test
    fun handlesCheckedStatesShorterThanSteps() {
        val steps = listOf(step("1"), step("2", critical = true), step("3"))
        val checked = listOf(true) // only one entry
        val result = UrgentProgressCalculator.calculate(steps, checked)
        assertEquals(1, result.completedSteps)
        assertEquals(3, result.totalSteps)
        assertEquals(UrgentStatus.NEEDS_ATTENTION, result.status)
        assertEquals(1, result.uncheckedCriticalSteps.size)
    }

    // --- Status labels ---

    @Test
    fun statusLabels_arePolish() {
        assertEquals("W toku", UrgentStatus.IN_PROGRESS.label)
        assertEquals("Wymaga uwagi", UrgentStatus.NEEDS_ATTENTION.label)
        assertEquals("Gotowe do zamknięcia", UrgentStatus.READY_TO_CLOSE.label)
    }
}
