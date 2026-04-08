package pl.mikoch.asystentsocjalny.core.model

data class Benefit(
    val id: String,
    val name: String,
    val description: String,
    val documents: List<String>,
    val note: String
)
