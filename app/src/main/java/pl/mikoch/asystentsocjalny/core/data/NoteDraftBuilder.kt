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
        additionalNotes: String,
        runningNotes: String = "",
        stepNotes: Map<Int, String> = emptyMap(),
        personsPresent: String = "",
        situationFlags: List<String> = emptyList()
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
            additionalNotes = additionalNotes,
            runningNotes = runningNotes,
            stepNotes = stepNotes,
            personsPresent = personsPresent,
            situationFlags = situationFlags
        )
    }

    fun formatToText(draft: NoteDraft): String = buildString {
        appendLine("NOTATKA SŁUŻBOWA – SZKIC")
        appendLine("(wymaga weryfikacji i uzupełnienia przed złożeniem)")
        appendLine()
        appendLine("Data: ${draft.date}")
        appendLine("Miejsce: ${draft.location}")
        appendLine("Pracownik: [imię i nazwisko]")
        if (draft.personsPresent.isNotBlank()) {
            appendLine("Osoby obecne: ${draft.personsPresent}")
        }
        appendLine()
        appendLine("Rodzaj sytuacji:")
        appendLine(draft.scenarioTitle)
        if (draft.situationFlags.isNotEmpty()) {
            appendLine()
            appendLine("Stan sytuacji:")
            draft.situationFlags.forEach { appendLine("- $it") }
        }
        appendLine()
        appendLine("Opis zastanej sytuacji:")
        appendLine(draft.situationDescription)
        appendLine()
        appendLine("Podjęte działania:")
        if (draft.completedSteps.isEmpty()) {
            appendLine("- [brak zaznaczonych kroków]")
        } else {
            draft.completedSteps.forEachIndexed { i, step ->
                appendLine("${i + 1}. $step")
                draft.stepNotes[i]?.takeIf { it.isNotBlank() }?.let { note ->
                    appendLine("   Notatka: $note")
                }
            }
        }
        if (draft.criticalCompletedSteps.isNotEmpty()) {
            appendLine()
            appendLine("Zrealizowane kroki krytyczne:")
            draft.criticalCompletedSteps.forEach { appendLine("- ✔ $it") }
        }
        if (draft.runningNotes.isNotBlank()) {
            appendLine()
            appendLine("Notatki bieżące:")
            appendLine(draft.runningNotes)
        }
        if (draft.additionalNotes.isNotBlank()) {
            appendLine()
            appendLine("Uwagi dodatkowe:")
            appendLine(draft.additionalNotes)
        }
        appendLine()
        appendLine("Dalsze kroki:")
        appendLine("- [do uzupełnienia przez pracownika]")
        appendLine()
        appendLine("Podpis: _________________________")
    }
}
