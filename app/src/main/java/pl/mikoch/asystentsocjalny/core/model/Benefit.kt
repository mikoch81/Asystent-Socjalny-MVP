package pl.mikoch.asystentsocjalny.core.model

data class Benefit(
    val id: String,
    val name: String,
    val description: String,
    val documents: List<String>,
    val conditions: List<String>,
    val note: String,
    val category: String = "Inne",
    val procedure: String? = null,
    val legalUpdatedAt: String = "",
    val legalReviewDueAt: String = "",
    val legalValidationStatus: String = "Wymaga walidacji",
    val validatedBy: String? = null
)
