package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.NoteDraft

class NoteDraftBuilderUrgentTest {

    @Test
    fun formatToText_containsScenarioTitle() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Przemoc domowa",
            scenarioDescription = "Opis scenariusza",
            completedSteps = listOf("Krok 1"),
            criticalCompletedSteps = emptyList(),
            location = "Warszawa",
            situationDescription = "Opis sytuacji",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Przemoc domowa"))
    }

    @Test
    fun formatToText_containsCompletedSteps() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Scenariusz",
            scenarioDescription = "Opis",
            completedSteps = listOf("Wezwij policję", "Sporządź notatkę"),
            criticalCompletedSteps = emptyList(),
            location = "",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Wezwij policję"))
        assertTrue(text.contains("Sporządź notatkę"))
    }

    @Test
    fun formatToText_containsCriticalStepsSection_whenPresent() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Scenariusz",
            scenarioDescription = "Opis",
            completedSteps = listOf("Krok krytyczny A"),
            criticalCompletedSteps = listOf("Krok krytyczny A"),
            location = "",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Zrealizowane kroki krytyczne"))
        assertTrue(text.contains("Krok krytyczny A"))
    }

    @Test
    fun formatToText_omitsCriticalSection_whenEmpty() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Scenariusz",
            scenarioDescription = "Opis",
            completedSteps = listOf("Zwykły krok"),
            criticalCompletedSteps = emptyList(),
            location = "",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertFalse(text.contains("Zrealizowane kroki krytyczne"))
    }

    @Test
    fun formatToText_containsLocationAndDate() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Test",
            scenarioDescription = "Opis",
            completedSteps = emptyList(),
            criticalCompletedSteps = emptyList(),
            location = "Kraków, ul. Główna 5",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Kraków, ul. Główna 5"))
        assertTrue(text.contains("Data:"))
    }

    @Test
    fun formatToText_containsAdditionalNotes_whenProvided() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Test",
            scenarioDescription = "Opis",
            completedSteps = emptyList(),
            criticalCompletedSteps = emptyList(),
            location = "",
            situationDescription = "",
            additionalNotes = "Dodatkowa uwaga testowa"
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Dodatkowa uwaga testowa"))
        assertTrue(text.contains("Uwagi dodatkowe"))
    }

    @Test
    fun formatToText_showsPlaceholder_whenNoStepsCompleted() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Test",
            scenarioDescription = "Opis",
            completedSteps = emptyList(),
            criticalCompletedSteps = emptyList(),
            location = "",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("[brak zaznaczonych kroków]"))
    }

    @Test
    fun build_usesScenarioDescription_whenSituationBlank() {
        val draft = NoteDraftBuilder.build(
            scenarioTitle = "Test",
            scenarioDescription = "Domyślny opis scenariusza",
            completedSteps = emptyList(),
            criticalCompletedSteps = emptyList(),
            location = "",
            situationDescription = "",
            additionalNotes = ""
        )
        val text = NoteDraftBuilder.formatToText(draft)

        assertTrue(text.contains("Domyślny opis scenariusza"))
    }
}
