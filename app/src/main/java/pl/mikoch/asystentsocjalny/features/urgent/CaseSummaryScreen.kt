package pl.mikoch.asystentsocjalny.features.urgent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.features.urgent.model.GuidanceUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus

@Composable
fun CaseSummaryScreen(
    scenario: UrgentScenarioUi,
    viewModel: UrgentViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val progress by viewModel.progress
    val noteText = viewModel.generatedNoteText.value
    val pdfReadiness by viewModel.pdfReadiness

    Scaffold(
        topBar = { TopAppBar(title = { Text("Podsumowanie sprawy") }) }
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
            // --- Title + Status ---
            Text(
                text = scenario.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            SummaryStatusRow(progress)

            // --- Completed steps ---
            SummaryCompletedSteps(scenario, viewModel)

            // --- Unchecked critical steps ---
            if (progress.uncheckedCriticalSteps.isNotEmpty()) {
                SummarySection(
                    title = "Niewykonane kroki krytyczne",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    progress.uncheckedCriticalSteps.forEach { step ->
                        Text(
                            text = "• ${step.text}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // --- Guidance ---
            scenario.guidance?.let { guidance ->
                SummaryGuidanceSection(guidance)
            }

            // --- Generated note ---
            if (noteText.isNotBlank()) {
                SummarySection(
                    title = "Notatka",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    SelectionContainer {
                        Text(
                            text = noteText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // --- Actions ---
            if (noteText.isNotBlank()) {
                Button(
                    onClick = {
                        viewModel.saveDraft()
                        Toast.makeText(context, "Zapisano wersję roboczą", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Zapisz jako robocze")
                }

                Button(
                    onClick = {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, noteText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Udostępnij notatkę"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Udostępnij")
                }

                Button(
                    onClick = {
                        viewModel.generatePdf()
                        Toast.makeText(context, "Wygenerowano PDF", Toast.LENGTH_SHORT).show()
                    },
                    enabled = pdfReadiness.enabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Generuj PDF do uzupełnienia i podpisu")
                }

                if (!pdfReadiness.enabled && pdfReadiness.reason.isNotBlank()) {
                    Text(
                        text = pdfReadiness.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Wróć do sprawy")
            }
        }
    }
}

@Composable
private fun SummaryStatusRow(progress: UrgentProgress) {
    val statusColor = when (progress.status) {
        UrgentStatus.READY_TO_CLOSE -> MaterialTheme.colorScheme.primary
        UrgentStatus.NEEDS_ATTENTION -> MaterialTheme.colorScheme.error
        UrgentStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = progress.progressText,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = progress.status.label,
            style = MaterialTheme.typography.labelLarge,
            color = statusColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SummaryCompletedSteps(
    scenario: UrgentScenarioUi,
    viewModel: UrgentViewModel
) {
    val completed = scenario.steps.filterIndexed { i, _ ->
        viewModel.checkedStates.getOrElse(i) { false }
    }

    if (completed.isNotEmpty()) {
        SummarySection(
            title = "Wykonane kroki (${completed.size})",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            completed.forEach { step ->
                Text(
                    text = "✓ ${step.text}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SummaryGuidanceSection(guidance: GuidanceUi) {
    SummarySection(
        title = "Co dalej",
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        if (guidance.notify.isNotEmpty()) {
            Text("Kogo powiadomić:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            guidance.notify.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
        if (guidance.documents.isNotEmpty()) {
            Text("Dokumenty:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            guidance.documents.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
        if (guidance.doNotMiss.isNotEmpty()) {
            Text("Nie pomiń:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            guidance.doNotMiss.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
        if (guidance.escalationRequired) {
            Text(
                text = "⚠ ${guidance.escalationNote}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummarySection(
    title: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides contentColor,
            content = content
        )
    }
}
