package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.ActionRecommendation
import pl.mikoch.asystentsocjalny.core.model.RecommendationPriority
import pl.mikoch.asystentsocjalny.core.model.RiskAssessment
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi
import pl.mikoch.asystentsocjalny.features.urgent.model.GuidanceUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress

object ActionRecommendationEngine {

    fun recommend(
        riskAssessment: RiskAssessment,
        progress: UrgentProgress,
        guidance: GuidanceUi?
    ): ActionRecommendation {
        val uncheckedCritical = progress.uncheckedCriticalSteps

        return when {
            uncheckedCritical.isNotEmpty() -> buildHighPriority(uncheckedCritical, guidance)
            progress.totalSteps > 0 && progress.completedSteps < progress.totalSteps -> buildMediumPriority(progress, guidance)
            else -> buildLowPriority(guidance)
        }
    }

    private fun buildHighPriority(
        uncheckedCritical: List<ChecklistStepUi>,
        guidance: GuidanceUi?
    ): ActionRecommendation {
        val actions = mutableListOf<String>()
        actions += "Należy niezwłocznie wykonać kroki krytyczne przed podjęciem dalszych czynności"
        uncheckedCritical.forEach { step ->
            actions += "→ ${step.text}"
        }
        if (guidance?.escalationRequired == true) {
            actions += "Wymagane jest przekazanie sprawy — należy Eskaluj zgodnie z obowiązującą procedurą"
        }
        actions += "Nie zamykaj sprawy do czasu zrealizowania wszystkich kroków krytycznych"

        val warnings = mutableListOf<String>()
        warnings += "Zaniechanie kroków krytycznych może skutkować zagrożeniem dla podopiecznego"
        if (guidance?.escalationRequired == true && guidance.escalationNote.isNotEmpty()) {
            warnings += guidance.escalationNote
        }

        return ActionRecommendation(
            title = "Wymagane pilne działanie",
            summary = "Stwierdzono niewykonane kroki krytyczne (${uncheckedCritical.size}). Wymagane jest ich niezwłoczne zrealizowanie.",
            actions = actions,
            warnings = warnings,
            priority = RecommendationPriority.HIGH
        )
    }

    private fun buildMediumPriority(
        progress: UrgentProgress,
        guidance: GuidanceUi?
    ): ActionRecommendation {
        val remaining = progress.totalSteps - progress.completedSteps
        val actions = mutableListOf<String>()
        actions += "Należy kontynuować realizację listy kontrolnej — pozostało $remaining kroków"
        actions += "Zaleca się bieżące uzupełnianie dokumentacji sprawy"
        if (guidance != null && guidance.documents.isNotEmpty()) {
            actions += "Wymagane jest przygotowanie dokumentów: ${guidance.documents.first()}"
        }
        if (guidance?.notify?.isNotEmpty() == true) {
            actions += "Należy powiadomić: ${guidance.notify.joinToString(", ")}"
        }

        val warnings = mutableListOf<String>()
        if (progress.completedSteps < progress.totalSteps / 2) {
            warnings += "Realizacja poniżej połowy — należy rozważyć priorytetyzację pozostałych czynności"
        }

        return ActionRecommendation(
            title = "Kontynuuj realizację",
            summary = "Kroki krytyczne zrealizowane. Należy dokończyć pozostałe $remaining kroków oraz uzupełnić dokumentację.",
            actions = actions,
            warnings = warnings,
            priority = RecommendationPriority.MEDIUM
        )
    }

    private fun buildLowPriority(guidance: GuidanceUi?): ActionRecommendation {
        val actions = mutableListOf<String>()
        actions += "Należy zweryfikować kompletność wykonanych kroków"
        actions += "Wymagane jest uzupełnienie danych do notatki służbowej"
        actions += "Zaleca się wygenerowanie i zweryfikowanie notatki służbowej — należy sporządzić notatkę przed zamknięciem"
        actions += "Sprawa spełnia warunki zamknięcia — należy przygotować dokumentację końcową"

        val warnings = mutableListOf<String>()
        if (guidance?.escalationRequired == true) {
            warnings += "Należy potwierdzić, że eskalacja została przeprowadzona zgodnie z procedurą"
        }

        return ActionRecommendation(
            title = "Sprawa do zamknięcia",
            summary = "Wszystkie kroki zrealizowane. Wymagane jest zweryfikowanie dokumentacji i przygotowanie zamknięcia sprawy.",
            actions = actions,
            warnings = warnings,
            priority = RecommendationPriority.LOW
        )
    }
}
