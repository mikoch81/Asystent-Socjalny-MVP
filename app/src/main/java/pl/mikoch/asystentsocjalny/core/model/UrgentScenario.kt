package pl.mikoch.asystentsocjalny.core.model

data class UrgentScenario(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<ChecklistStep>,
    val guidance: UrgentGuidance? = null
)

data class ChecklistStep(
    val id: String,
    val text: String,
    val isCritical: Boolean
)

data class UrgentGuidance(
    val notify: List<String>,
    val documents: List<String>,
    val doNotMiss: List<String>,
    val escalationRequired: Boolean,
    val escalationNote: String
)
