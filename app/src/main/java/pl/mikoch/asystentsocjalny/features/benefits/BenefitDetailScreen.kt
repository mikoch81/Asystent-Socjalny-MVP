package pl.mikoch.asystentsocjalny.features.benefits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Benefit

@Composable
fun BenefitDetailScreen(benefit: Benefit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(benefit.name) }) }
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
            LegalStatusBanner(
                status = benefit.legalValidationStatus,
                updatedAt = benefit.legalUpdatedAt,
                reviewDueAt = benefit.legalReviewDueAt,
                validatedBy = benefit.validatedBy
            )

            Text(
                text = "Kategoria: ${benefit.category}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = benefit.description,
                style = MaterialTheme.typography.bodyLarge
            )

            BenefitSection("Wymagane dokumenty", benefit.documents)
            BenefitSection("Warunki ogólne", benefit.conditions)

            Section("Uwagi") {
                Text(
                    text = benefit.note,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            benefit.procedure?.let { linkedProcedure ->
                Section("Powiązana procedura") {
                    Text(
                        text = linkedProcedure,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitSection(title: String, items: List<String>) {
    if (items.isEmpty()) return
    Section(title) {
        items.forEach { item ->
            Text(
                text = "• $item",
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
private fun LegalStatusBanner(
    status: String,
    updatedAt: String,
    reviewDueAt: String,
    validatedBy: String? = null
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
        colors = CardDefaults.cardColors(containerColor = background)
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
            if (!validatedBy.isNullOrBlank()) {
                Text(
                    text = "Zweryfikowane przez: $validatedBy",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}
