package pl.mikoch.asystentsocjalny.core.model

data class UrgentScenario(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<ChecklistStep>
)

data class ChecklistStep(
    val id: String,
    val text: String,
    val isCritical: Boolean
)
