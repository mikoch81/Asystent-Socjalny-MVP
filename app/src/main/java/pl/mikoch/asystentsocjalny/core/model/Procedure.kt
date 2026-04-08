package pl.mikoch.asystentsocjalny.core.model

data class Procedure(
    val id: String,
    val title: String,
    val category: String,
    val situation: String,
    val severity: String,
    val nowSteps: List<String>,
    val notify: List<String>,
    val doNotMiss: List<String>,
    val legalBasis: List<String>,
    val escalation: String,
    val documents: List<String>
)
