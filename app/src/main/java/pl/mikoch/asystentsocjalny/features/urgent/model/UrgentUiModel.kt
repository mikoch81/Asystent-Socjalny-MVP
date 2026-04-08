package pl.mikoch.asystentsocjalny.features.urgent.model

data class UrgentScenarioUi(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<ChecklistStepUi>
)

data class ChecklistStepUi(
    val id: String,
    val text: String,
    val isCritical: Boolean
)

enum class UrgentStatus(val label: String) {
    IN_PROGRESS("W toku"),
    NEEDS_ATTENTION("Wymaga uwagi"),
    READY_TO_CLOSE("Gotowe do zamknięcia")
}

data class UrgentProgress(
    val totalSteps: Int,
    val completedSteps: Int,
    val progressText: String,
    val status: UrgentStatus,
    val uncheckedCriticalSteps: List<ChecklistStepUi>
)
