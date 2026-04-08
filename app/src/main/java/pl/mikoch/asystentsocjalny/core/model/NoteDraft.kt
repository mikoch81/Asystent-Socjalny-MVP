package pl.mikoch.asystentsocjalny.core.model

data class NoteDraft(
    val date: String,
    val location: String,
    val scenarioTitle: String,
    val situationDescription: String,
    val completedSteps: List<String>,
    val criticalCompletedSteps: List<String>,
    val recommendations: String,
    val additionalNotes: String
)
