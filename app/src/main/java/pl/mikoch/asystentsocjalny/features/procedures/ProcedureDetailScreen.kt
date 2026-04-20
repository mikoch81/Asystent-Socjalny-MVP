package pl.mikoch.asystentsocjalny.features.procedures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.ContactInfo
import pl.mikoch.asystentsocjalny.core.model.Procedure

@Composable
fun ProcedureDetailScreen(procedure: Procedure) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(procedure.title) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary + severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = procedure.situation,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                SeverityBadge(procedure.severity)
            }

            LegalStatusBanner(
                status = procedure.legalValidationStatus,
                updatedAt = procedure.legalUpdatedAt,
                reviewDueAt = procedure.legalReviewDueAt
            )

            ProcedureSection("Co zrobić teraz", procedure.nowSteps)
            ProcedureSection("Kogo powiadomić", procedure.notify)
            ProcedureSection("Czego nie pominąć", procedure.doNotMiss)
            ProcedureSection("Podstawa prawna / źródło", procedure.legalBasis)

            Section("Czy wymagana konsultacja") {
                Text(
                    text = procedure.escalation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            ProcedureSection("Jakie dokumenty przygotować", procedure.documents)

            ProcedureSection("Powiązane świadczenia", procedure.relatedBenefits)

            procedure.contact?.let {
                ContactSection(contact = it)
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val color = when (severity) {
        "Wysoki" -> MaterialTheme.colorScheme.error
        "Średni" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
    Text(
        text = severity,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun ProcedureSection(title: String, items: List<String>) {
    if (items.isEmpty()) return
    Section(title) {
        items.forEachIndexed { index, item ->
            Text(
                text = "${index + 1}. $item",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun ContactSection(contact: ContactInfo) {
    Section("Kontakt MOPS") {
        Text(text = contact.unitName, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Telefon: ${contact.phone}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Godziny: ${contact.hours}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Adres: ${contact.address}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LegalStatusBanner(
    status: String,
    updatedAt: String,
    reviewDueAt: String
) {
    val isValidated = status.equals("Zweryfikowane", ignoreCase = true)
    val background = if (isValidated) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isValidated) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Status prawny: $status",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            if (updatedAt.isNotBlank()) {
                Text(
                    text = "Ostatnia aktualizacja: $updatedAt",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
            if (reviewDueAt.isNotBlank()) {
                Text(
                    text = "Przegląd wymagany do: $reviewDueAt",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}
