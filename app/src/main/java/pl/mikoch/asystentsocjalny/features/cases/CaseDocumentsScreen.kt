package pl.mikoch.asystentsocjalny.features.cases

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.PdfFileHelper
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaseDocumentsScreen(
    caseId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val store = remember { CaseDocumentStore(context) }
    val scope = rememberCoroutineScope()
    var documents by remember { mutableStateOf<List<CaseDocument>>(emptyList()) }
    var previewDocument by remember { mutableStateOf<CaseDocument?>(null) }

    LaunchedEffect(caseId) {
        documents = store.loadForCase(caseId)
    }

    BaseScrollableScreen(title = "Dokumenty sprawy") {
        if (documents.isEmpty()) {
            item(key = "empty") {
                EmptyStateMessage(
                    title = "Brak dokumentów dla tej sprawy",
                    subtitle = "Dokumenty pojawią się po zapisaniu notatki lub wygenerowaniu PDF."
                )
            }
        } else {
            items(documents.size, key = { documents[it].documentId }) { index ->
                val doc = documents[index]
                DocumentCard(
                    document = doc,
                    onOpen = {
                        when (doc.type) {
                            DocumentType.NOTE_DRAFT -> {
                                previewDocument = doc
                            }
                            DocumentType.PDF_DRAFT -> {
                                val file = File(doc.filePath)
                                if (file.exists()) {
                                    val opened = PdfFileHelper.openPdf(context, file)
                                    if (!opened) {
                                        Toast.makeText(
                                            context,
                                            "Brak aplikacji do otwarcia PDF",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Plik PDF nie istnieje",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    onShare = {
                        when (doc.type) {
                            DocumentType.NOTE_DRAFT -> {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, doc.textContent)
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    Intent.createChooser(sendIntent, "Udostępnij notatkę")
                                )
                            }
                            DocumentType.PDF_DRAFT -> {
                                val file = File(doc.filePath)
                                if (file.exists()) {
                                    PdfFileHelper.sharePdf(context, file)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Plik PDF nie istnieje",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    onDelete = {
                        scope.launch {
                            if (doc.type == DocumentType.PDF_DRAFT && doc.filePath.isNotBlank()) {
                                File(doc.filePath).delete()
                            }
                            store.delete(doc.documentId, caseId)
                            documents = store.loadForCase(caseId)
                        }
                    }
                )
            }
        }

        item(key = "btn_back") {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Wróć")
            }
        }
    }

    previewDocument?.let { doc ->
        AlertDialog(
            onDismissRequest = { previewDocument = null },
            title = { Text(doc.title) },
            text = {
                SelectionContainer {
                    Text(
                        text = doc.textContent,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { previewDocument = null }) {
                    Text("Zamknij")
                }
            }
        )
    }
}

@Composable
private fun DocumentCard(
    document: CaseDocument,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = document.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = formatDocTimestamp(document.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onOpen) { Text("Otwórz") }
                TextButton(onClick = onShare) { Text("Udostępnij") }
                TextButton(onClick = onDelete) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun formatDocTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl"))
    return sdf.format(Date(millis))
}
