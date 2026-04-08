package pl.mikoch.asystentsocjalny.features.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(procedures: List<Procedure>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedProcedure by remember { mutableStateOf<Procedure?>(procedures.firstOrNull()) }
    var generatedText by remember { mutableStateOf("") }

    BaseScrollableScreen(title = "Generator notatki") {
        if (procedures.isEmpty()) {
            item(key = "empty") {
                EmptyStateMessage(
                    title = "Brak dostępnych procedur",
                    subtitle = "Nie udało się wczytać danych do generatora."
                )
            }
        } else {
            item(key = "intro") {
                Text(
                    "Wybierz procedurę i wygeneruj szkic notatki do dalszego uzupełnienia.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item(key = "dropdown") {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProcedure?.title.orEmpty(),
                        onValueChange = {},
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        label = { Text("Procedura") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        procedures.forEach { procedure ->
                            DropdownMenuItem(
                                text = { Text(procedure.title) },
                                onClick = {
                                    selectedProcedure = procedure
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item(key = "btn_generate") {
                Button(
                    onClick = {
                        selectedProcedure?.let { procedure ->
                            generatedText = buildNoteDraft(procedure)
                        }
                    }
                ) {
                    Text("Generuj szkic")
                }
            }

            item(key = "result") {
                SelectionContainer {
                    Text(
                        text = generatedText.ifBlank {
                            "Tutaj pojawi się szkic notatki."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

internal fun buildNoteDraft(procedure: Procedure): String {
    return buildString {
        appendLine("NOTATKA SŁUŻBOWA – SZKIC")
        appendLine()
        appendLine("Data: [uzupełnij]")
        appendLine("Miejsce: [uzupełnij]")
        appendLine("Pracownik: [uzupełnij]")
        appendLine()
        appendLine("Rodzaj sytuacji:")
        appendLine(procedure.title)
        appendLine()
        appendLine("Opis zastanej sytuacji:")
        appendLine("[uzupełnij opis zdarzenia bez danych nadmiarowych]")
        appendLine()
        appendLine("Podjęte działania:")
        procedure.nowSteps.forEach { appendLine("- $it") }
        appendLine()
        appendLine("Powiadomione podmioty:")
        procedure.notify.forEach { appendLine("- $it") }
        appendLine()
        appendLine("Dokumenty / dalsze kroki:")
        procedure.documents.forEach { appendLine("- $it") }
        appendLine()
        appendLine("Uwagi:")
        appendLine(procedure.escalation)
    }
}
