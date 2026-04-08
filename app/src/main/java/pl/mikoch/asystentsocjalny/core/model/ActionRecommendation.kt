package pl.mikoch.asystentsocjalny.core.model

enum class RecommendationPriority(val label: String) {
    LOW("Niski"),
    MEDIUM("Średni"),
    HIGH("Wysoki")
}

data class ActionRecommendation(
    val title: String,
    val summary: String,
    val actions: List<String>,
    val warnings: List<String>,
    val priority: RecommendationPriority
)
