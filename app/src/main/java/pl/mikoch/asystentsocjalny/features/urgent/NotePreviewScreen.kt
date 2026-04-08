package pl.mikoch.asystentsocjalny.features.urgent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen

@Composable
fun NotePreviewScreen(
    noteText: String,
    pdfReadiness: PdfReadiness,
    onSaveDraft: () -> Unit,
    onGeneratePdf: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    BaseScrollableScreen(title = "Podgląd notatki") {
        item(key = "note_content") {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NoteFormattedContent(noteText)
                }
            }
        }

        item(key = "btn_save") {
            Button(
                onClick = {
                    onSaveDraft()
                    Toast.makeText(context, "Zapisano wersję roboczą", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Zapisz jako robocze")
            }
        }

        item(key = "btn_share") {
            Button(
                onClick = { shareNote(context, noteText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Udostępnij")
            }
        }

        item(key = "btn_pdf") {
            Button(
                onClick = {
                    onGeneratePdf()
                    Toast.makeText(context, "Wygenerowano PDF", Toast.LENGTH_SHORT).show()
                },
                enabled = pdfReadiness.enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Generuj PDF do uzupełnienia i podpisu")
            }
        }

        if (!pdfReadiness.enabled && pdfReadiness.reason.isNotBlank()) {
            item(key = "pdf_reason") {
                Text(
                    text = pdfReadiness.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "btn_back") {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Wróć")
            }
        }
    }
}

@Composable
private fun NoteFormattedContent(noteText: String) {
    if (noteText.isBlank()) {
        Text(
            text = "Brak wygenerowanej notatki.",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    val sectionHeaders = setOf(
        "NOTATKA SŁUŻBOWA – SZKIC",
        "Rodzaj sytuacji:",
        "Opis zastanej sytuacji:",
        "Podjęte działania:",
        "Zrealizowane kroki krytyczne:",
        "Uwagi dodatkowe:",
        "Dalsze kroki:"
    )

    val lines = noteText.lines()
    var isFirstLine = true

    for (line in lines) {
        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> Spacer(modifier = Modifier.height(6.dp))
            trimmed in sectionHeaders -> {
                if (!isFirstLine) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Text(
                    text = trimmed,
                    style = if (isFirstLine) MaterialTheme.typography.titleMedium
                            else MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                isFirstLine = false
            }
            trimmed.startsWith("(wymaga") -> {
                Text(
                    text = trimmed,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trimmed.startsWith("Podpis:") -> {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trimmed,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            else -> {
                Text(
                    text = trimmed,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun shareNote(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Udostępnij notatkę"))
}
