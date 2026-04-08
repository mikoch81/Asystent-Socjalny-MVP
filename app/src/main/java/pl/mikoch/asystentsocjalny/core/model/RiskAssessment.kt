package pl.mikoch.asystentsocjalny.core.model

enum class RiskLevel(val label: String) {
    LOW("Niskie"),
    MEDIUM("Średnie"),
    HIGH("Wysokie")
}

data class RiskAssessment(
    val level: RiskLevel,
    val reasons: List<String>
)
