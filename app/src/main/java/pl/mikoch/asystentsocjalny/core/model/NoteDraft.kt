package pl.mikoch.asystentsocjalny.core.model

data class NoteDraft(
    val date: String,
    val location: String,
    val scenarioTitle: String,
    val situationDescription: String,
    val completedSteps: List<String>,
    val criticalCompletedSteps: List<String>,
    val recommendations: String,
    val additionalNotes: String,
    val runningNotes: String = "",
    val stepNotes: Map<Int, String> = emptyMap(),
    val personsPresent: String = "",
    val situationFlags: List<String> = emptyList()
)

object SituationFlag {
    const val ALCOHOL = "Alkohol"
    const val AGGRESSION = "Agresja"
    const val LIFE_THREAT = "Zagrożenie zdrowia/życia"
    const val CHILDREN_PRESENT = "Obecność dzieci"
    const val HOMELESSNESS = "Bezdomność"

    val ALL = listOf(ALCOHOL, AGGRESSION, LIFE_THREAT, CHILDREN_PRESENT, HOMELESSNESS)
}
