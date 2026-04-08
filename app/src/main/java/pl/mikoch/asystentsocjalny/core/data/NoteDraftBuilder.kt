package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.NoteDraft
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NoteDraftBuilder {

    fun build(
        scenarioTitle: String,
        scenarioDescription: String,
        completedSteps: List<String>,
        criticalCompletedSteps: List<String>,
        location: String,
        situationDescription: String,
        additionalNotes: String
    ): NoteDraft {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return NoteDraft(
            date = date,
            location = location.ifBlank { "[nie podano]" },
            scenarioTitle = scenarioTitle,
            situationDescription = situationDescription.ifBlank { scenarioDescription },
            completedSteps = completedSteps,
            criticalCompletedSteps = criticalCompletedSteps,
            recommendations = "",
            additionalNotes = additionalNotes
        )
    }

    fun formatToText(draft: NoteDraft): String = buildString {
        appendLine("NOTATKA SŁUŻBOWA – SZKIC")
        appendLine()
        appendLine("Data: ${draft.date}")
        appendLine("Miejsce: ${draft.location}")
        appendLine()
        appendLine("Rodzaj sytuacji:")
        appendLine(draft.scenarioTitle)
        appendLine()
        appendLine("Opis sytuacji:")
        appendLine(draft.situationDescription)
        appendLine()
        appendLine("Wykonane działania:")
        if (draft.completedSteps.isEmpty()) {
            appendLine("- [brak zaznaczonych kroków]")
        } else {
            draft.completedSteps.forEach { appendLine("- $it") }
        }
        if (draft.criticalCompletedSteps.isNotEmpty()) {
            appendLine()
            appendLine("Zrealizowane kroki krytyczne:")
            draft.criticalCompletedSteps.forEach { appendLine("- ✔ $it") }
        }
        if (draft.additionalNotes.isNotBlank()) {
            appendLine()
            appendLine("Uwagi dodatkowe:")
            appendLine(draft.additionalNotes)
        }
    }
}
