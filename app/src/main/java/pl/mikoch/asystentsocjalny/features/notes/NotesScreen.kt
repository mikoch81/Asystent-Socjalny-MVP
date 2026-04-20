package pl.mikoch.asystentsocjalny.features.notes

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.mikoch.asystentsocjalny.core.data.PdfDraftContent
import pl.mikoch.asystentsocjalny.core.data.PdfDraftGenerator
import pl.mikoch.asystentsocjalny.core.data.PdfFileHelper
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage
import androidx.compose.runtime.LaunchedEffect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    procedures: List<Procedure>,
    onCreateCase: (procedureId: String, procedureTitle: String, noteText: String) -> Unit = { _, _, _ -> },
    viewModel: NotesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val workerProfile by viewModel.profileFlow.collectAsState(initial = WorkerProfile.EMPTY)
    val lastLocation by viewModel.lastLocationFlow.collectAsState(initial = "")
    var location by remember { mutableStateOf("") }
    LaunchedEffect(lastLocation) {
        if (location.isBlank() && lastLocation.isNotBlank()) location = lastLocation
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedProcedure by remember { mutableStateOf<Procedure?>(procedures.firstOrNull()) }
    var draftText by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Restore saved draft when procedure changes
    val hasDraft = draftText.isNotBlank()

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
                    "Wybierz procedurę, wygeneruj szkic i edytuj go przed zapisaniem lub udostępnieniem.",
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
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                                    // Load saved draft for newly selected procedure
                                    val saved = viewModel.loadDraft(procedure.id)
                                    if (saved != null) {
                                        draftText = saved
                                        isEditing = false
                                    } else {
                                        draftText = ""
                                        isEditing = false
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item(key = "location") {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Miejsce zdarzenia") },
                    placeholder = { Text("np. ul. Piłsudskiego 12, Zgierz") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(key = "btn_generate") {
                Button(
                    onClick = {
                        selectedProcedure?.let { procedure ->
                            draftText = buildNoteDraft(procedure, workerProfile, location)
                            isEditing = false
                            viewModel.rememberLocation(location)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generuj szkic")
                }
            }

            if (hasDraft) {
                item(key = "divider_draft") {
                    HorizontalDivider()
                }

                item(key = "draft_content") {
                    if (isEditing) {
                        OutlinedTextField(
                            value = draftText,
                            onValueChange = { draftText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Treść notatki") },
                            minLines = 10,
                            maxLines = 30,
                            supportingText = {
                                Text(
                                    text = "Znaki: ${draftText.length}" +
                                        if (draftText.length < 50) " • minimum 50, by wygenerować PDF" else "",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    } else {
                        SelectionContainer {
                            Text(
                                text = draftText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item(key = "btn_edit") {
                    OutlinedButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isEditing) "Zakończ edycję" else "Edytuj szkic")
                    }
                }

                item(key = "divider_actions") {
                    HorizontalDivider()
                }

                item(key = "btn_save_draft") {
                    Button(
                        onClick = {
                            selectedProcedure?.let { procedure ->
                                viewModel.saveDraft(procedure.id, draftText)
                                Toast.makeText(context, "Zapisano wersję roboczą", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Zapisz wersję roboczą")
                    }
                }

                item(key = "btn_create_case") {
                    Button(
                        onClick = {
                            selectedProcedure?.let { procedure ->
                                viewModel.saveDraft(procedure.id, draftText)
                                onCreateCase(procedure.id, procedure.title, draftText)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Utwórz sprawę")
                    }
                }

                item(key = "btn_share") {
                    OutlinedButton(
                        onClick = { shareNoteText(context, draftText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Udostępnij tekst")
                    }
                }

                item(key = "btn_pdf") {
                    OutlinedButton(
                        onClick = {
                            selectedProcedure?.let { procedure ->
                                generateAndOpenPdf(context, procedure, draftText, workerProfile)
                            }
                        },
                        enabled = draftText.length >= 50,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Generuj PDF roboczy")
                    }
                }
            }

            if (!hasDraft) {
                item(key = "placeholder") {
                    Text(
                        text = "Tutaj pojawi się szkic notatki.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun shareNoteText(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Udostępnij notatkę"))
}

private fun generateAndOpenPdf(
    context: Context,
    procedure: Procedure,
    noteText: String,
    worker: WorkerProfile
) {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val content = PdfDraftContent(
        scenarioTitle = procedure.title,
        date = date,
        caseStatus = "Szkic",
        riskLevel = procedure.severity,
        recommendation = "",
        noteText = noteText,
        worker = worker
    )
    val file = PdfDraftGenerator.generate(context, content)
    val path = PdfFileHelper.displayPath(file)
    val opened = PdfFileHelper.openPdf(context, file)
    if (opened) {
        Toast.makeText(context, "PDF zapisany: $path", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, "PDF zapisany: $path\nBrak aplikacji do otwarcia PDF", Toast.LENGTH_LONG).show()
    }
}

internal fun buildNoteDraft(
    procedure: Procedure,
    worker: WorkerProfile = WorkerProfile.EMPTY,
    location: String = ""
): String {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val workerLine = if (worker.isComplete) worker.signatureLine else "[uzupełnij]"
    val locationLine = if (location.isNotBlank()) location else "[uzupełnij]"
    return buildString {
        appendLine("NOTATKA SŁUŻBOWA – SZKIC")
        appendLine()
        appendLine("Data: $date")
        appendLine("Miejsce: $locationLine")
        appendLine("Pracownik: $workerLine")
        if (worker.phone.isNotBlank()) appendLine("Telefon służbowy: ${worker.phone}")
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
