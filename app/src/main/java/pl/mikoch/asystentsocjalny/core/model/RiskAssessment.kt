package pl.mikoch.asystentsocjalny.core.model

enum class RiskLevel(val label: String) {
    LOW("Niski"),
    MEDIUM("Średni"),
    HIGH("Wysoki")
}

data class RiskAssessment(
    val level: RiskLevel,
    val reasons: List<String>
)
