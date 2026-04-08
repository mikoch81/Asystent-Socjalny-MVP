package pl.mikoch.asystentsocjalny.core.model

import pl.mikoch.asystentsocjalny.core.model.RiskLevel

enum class CaseStatus(val label: String) {
    DRAFT("Szkic"),
    IN_PROGRESS("W toku"),
    READY_TO_CLOSE("Gotowe do zamknięcia")
}

enum class CaseLifecycle(val label: String) {
    ACTIVE("Aktywna"),
    READY_TO_CLOSE("Do zamknięcia"),
    CLOSED("Zamknięta"),
    ARCHIVED("Zarchiwizowana")
}

data class CaseRecord(
    val caseId: String,
    val scenarioId: String,
    val scenarioTitle: String,
    val status: CaseStatus,
    val riskLevel: RiskLevel,
    val updatedAt: Long,
    val isDraft: Boolean = true,
    val locationPreview: String = "",
    val lifecycle: CaseLifecycle = CaseLifecycle.ACTIVE
)
