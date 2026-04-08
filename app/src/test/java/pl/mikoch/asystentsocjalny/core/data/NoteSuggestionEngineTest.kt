package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.core.model.SituationFlag

class NoteSuggestionEngineTest {

    private fun input(
        completedStepTexts: List<String> = emptyList(),
        uncheckedCriticalTexts: List<String> = emptyList(),
        riskLevel: RiskLevel = RiskLevel.LOW,
        scenarioTitle: String = "Test",
        situationFlags: List<String> = emptyList(),
        additionalNotes: String = ""
    ) = NoteSuggestionEngine.Input(
        completedStepTexts = completedStepTexts,
        uncheckedCriticalTexts = uncheckedCriticalTexts,
        riskLevel = riskLevel,
        scenarioTitle = scenarioTitle,
        situationFlags = situationFlags,
        additionalNotes = additionalNotes
    )

    @Test
    fun alwaysContainsFallback() {
        val result = NoteSuggestionEngine.suggest(input())
        assertTrue(result.contains("Przeprowadzono interwencję zgodnie z obowiązującą procedurą."))
    }

    @Test
    fun alcoholFlag_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(situationFlags = listOf(SituationFlag.ALCOHOL))
        )
        assertTrue(result.any { it.contains("alkoholu") })
    }

    @Test
    fun alcoholKeywordInNotes_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(additionalNotes = "Osoba była pijany")
        )
        assertTrue(result.any { it.contains("alkoholu") })
    }

    @Test
    fun aggressionFlag_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(situationFlags = listOf(SituationFlag.AGGRESSION))
        )
        assertTrue(result.any { it.contains("agresywne") })
    }

    @Test
    fun lifeThreatFlag_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(situationFlags = listOf(SituationFlag.LIFE_THREAT))
        )
        assertTrue(result.any { it.contains("zagrożenie zdrowia lub życia") })
    }

    @Test
    fun childrenPresentFlag_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(situationFlags = listOf(SituationFlag.CHILDREN_PRESENT))
        )
        assertTrue(result.any { it.contains("dzieci") })
    }

    @Test
    fun homelessnessFlag_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(situationFlags = listOf(SituationFlag.HOMELESSNESS))
        )
        assertTrue(result.any { it.contains("bezdomn") })
    }

    @Test
    fun highRisk_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(riskLevel = RiskLevel.HIGH)
        )
        assertTrue(result.any { it.contains("wysokiego ryzyka") })
    }

    @Test
    fun mediumRisk_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(riskLevel = RiskLevel.MEDIUM)
        )
        assertTrue(result.any { it.contains("średniego ryzyka") })
    }

    @Test
    fun lowRisk_noRiskSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(riskLevel = RiskLevel.LOW)
        )
        assertFalse(result.any { it.contains("ryzyka") })
    }

    @Test
    fun uncheckedCritical_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(uncheckedCriticalTexts = listOf("Krok krytyczny"))
        )
        assertTrue(result.any { it.contains("krytyczne") })
    }

    @Test
    fun allCriticalDone_producesSafetySuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(
                completedStepTexts = listOf("Zapewnij bezpieczeństwo"),
                uncheckedCriticalTexts = emptyList()
            )
        )
        assertTrue(result.any { it.contains("zapewnienie bezpieczeństwa") })
    }

    @Test
    fun policeStepChecked_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(completedStepTexts = listOf("Wezwij Policję (997/112)"))
        )
        assertTrue(result.any { it.contains("służby") })
    }

    @Test
    fun blueCardStep_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(completedStepTexts = listOf("Wypełnij formularz Niebieskiej Karty"))
        )
        assertTrue(result.any { it.contains("Niebieskiej Karty") })
    }

    @Test
    fun childScenario_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(scenarioTitle = "Podejrzenie przemocy wobec dziecka")
        )
        assertTrue(result.any { it.contains("dziecka") })
    }

    @Test
    fun homelessScenario_producesSuggestion() {
        val result = NoteSuggestionEngine.suggest(
            input(scenarioTitle = "Osoba bezdomna w okresie zimowym")
        )
        assertTrue(result.any { it.contains("warunki bytowe") })
    }

    @Test
    fun noDuplicates() {
        val result = NoteSuggestionEngine.suggest(
            input(
                situationFlags = listOf(SituationFlag.ALCOHOL, SituationFlag.AGGRESSION),
                riskLevel = RiskLevel.HIGH,
                completedStepTexts = listOf("Wezwij Policję (997/112)"),
                scenarioTitle = "Podejrzenie przemocy wobec dziecka"
            )
        )
        assertTrue(result.size == result.distinct().size)
    }

    @Test
    fun fullScenario_producesMultipleSuggestions() {
        val result = NoteSuggestionEngine.suggest(
            input(
                completedStepTexts = listOf(
                    "Oceń bezpieczeństwo dziecka",
                    "Wypełnij formularz Niebieskiej Karty"
                ),
                uncheckedCriticalTexts = emptyList(),
                riskLevel = RiskLevel.HIGH,
                scenarioTitle = "Podejrzenie przemocy wobec dziecka",
                situationFlags = listOf(SituationFlag.AGGRESSION, SituationFlag.CHILDREN_PRESENT)
            )
        )
        assertTrue(result.size >= 5)
        assertTrue(result.last() == "Przeprowadzono interwencję zgodnie z obowiązującą procedurą.")
    }
}
