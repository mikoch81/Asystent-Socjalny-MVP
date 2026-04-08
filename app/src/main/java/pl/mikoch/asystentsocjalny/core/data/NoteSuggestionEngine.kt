package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.core.model.SituationFlag

/**
 * Rule-based engine producing formal note suggestions
 * based on current intervention state.
 * No external API calls — fully offline and deterministic.
 */
object NoteSuggestionEngine {

    data class Input(
        val completedStepTexts: List<String>,
        val uncheckedCriticalTexts: List<String>,
        val riskLevel: RiskLevel,
        val scenarioTitle: String,
        val situationFlags: List<String>,
        val additionalNotes: String
    )

    fun suggest(input: Input): List<String> {
        val result = mutableListOf<String>()

        // --- situation-flag rules ---
        if (SituationFlag.ALCOHOL in input.situationFlags ||
            input.additionalNotes.contains("pijany", ignoreCase = true) ||
            input.additionalNotes.contains("alkohol", ignoreCase = true)
        ) {
            result += "Stwierdzono obecność osoby pod wpływem alkoholu."
        }

        if (SituationFlag.AGGRESSION in input.situationFlags) {
            result += "Zaobserwowano zachowania agresywne."
        }

        if (SituationFlag.LIFE_THREAT in input.situationFlags) {
            result += "Sytuacja stwarzała bezpośrednie zagrożenie zdrowia lub życia."
        }

        if (SituationFlag.CHILDREN_PRESENT in input.situationFlags) {
            result += "W miejscu interwencji obecne były dzieci."
        }

        if (SituationFlag.HOMELESSNESS in input.situationFlags) {
            result += "Osoba objęta interwencją jest bezdomna lub zagrożona bezdomnością."
        }

        // --- risk-level rules ---
        when (input.riskLevel) {
            RiskLevel.HIGH -> result += "Sytuacja została oceniona jako wysokiego ryzyka."
            RiskLevel.MEDIUM -> result += "Sytuacja została oceniona jako średniego ryzyka."
            RiskLevel.LOW -> { /* no suggestion for low risk */ }
        }

        // --- critical-step rules ---
        if (input.uncheckedCriticalTexts.isNotEmpty()) {
            result += "Nie wszystkie działania krytyczne zostały jeszcze zrealizowane."
        }
        if (input.uncheckedCriticalTexts.isEmpty() && input.completedStepTexts.isNotEmpty()) {
            result += "Podjęto działania mające na celu zapewnienie bezpieczeństwa."
        }

        // --- step-content rules ---
        val allTexts = input.completedStepTexts.joinToString(" ").lowercase()

        if (allTexts.contains("policj") || allTexts.contains("997") || allTexts.contains("112")) {
            result += "Powiadomiono odpowiednie służby zgodnie z procedurą."
        }

        if (allTexts.contains("niebiesk") && allTexts.contains("kart")) {
            result += "Uruchomiono procedurę Niebieskiej Karty."
        }

        // --- scenario-type rules ---
        val titleLower = input.scenarioTitle.lowercase()
        if (titleLower.contains("dziec") || titleLower.contains("nielet")) {
            result += "Dokonano oceny bezpieczeństwa dziecka."
        }

        if (titleLower.contains("bezdomn") || titleLower.contains("zimow")) {
            result += "Oceniono stan zdrowia osoby i warunki bytowe."
        }

        // --- generic fallback (always last) ---
        result += "Przeprowadzono interwencję zgodnie z obowiązującą procedurą."

        return result.distinct()
    }
}
