package pl.mikoch.asystentsocjalny.features.urgent.model

object UrgentProgressCalculator {

    fun calculate(steps: List<ChecklistStepUi>, checkedStates: List<Boolean>): UrgentProgress {
        val total = steps.size
        val completed = checkedStates.count { it }

        val uncheckedCritical = steps.filterIndexed { index, step ->
            step.isCritical && !(checkedStates.getOrElse(index) { false })
        }

        val status = when {
            total > 0 && completed == total -> UrgentStatus.READY_TO_CLOSE
            uncheckedCritical.isNotEmpty() -> UrgentStatus.NEEDS_ATTENTION
            else -> UrgentStatus.IN_PROGRESS
        }

        return UrgentProgress(
            totalSteps = total,
            completedSteps = completed,
            progressText = "Wykonano $completed z $total kroków",
            status = status,
            uncheckedCriticalSteps = uncheckedCritical
        )
    }
}
