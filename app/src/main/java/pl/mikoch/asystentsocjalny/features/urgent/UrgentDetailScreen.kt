package pl.mikoch.asystentsocjalny.features.urgent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus

@Composable
fun UrgentDetailScreen(
    scenario: UrgentScenarioUi,
    viewModel: UrgentViewModel,
    onNavigateToPreview: () -> Unit
) {
    LaunchedEffect(scenario.id) {
        viewModel.initDetailState(scenario)
    }

    val progress by viewModel.progress

    Scaffold(
        topBar = { TopAppBar(title = { Text(scenario.title) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = scenario.description,
                style = MaterialTheme.typography.bodyLarge
            )

            // --- Progress & Status ---
            UrgentProgressSection(progress)

            // --- Unchecked critical steps ---
            if (progress.uncheckedCriticalSteps.isNotEmpty()) {
                UrgentUncheckedCriticalSection(progress.uncheckedCriticalSteps)
            }

            Text(
                text = "Checklist",
                style = MaterialTheme.typography.titleMedium
            )
            scenario.steps.forEachIndexed { index, step ->
                UrgentChecklistRow(
                    checked = viewModel.checkedStates.getOrElse(index) { false },
                    onCheckedChange = { viewModel.checkedStates[index] = it },
                    step = step
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Dane do notatki", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = viewModel.location.value,
                onValueChange = { viewModel.location.value = it },
                label = { Text("Miejsce interwencji") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = viewModel.situationDescription.value,
                onValueChange = { viewModel.situationDescription.value = it },
                label = { Text("Opis sytuacji") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = viewModel.additionalNotes.value,
                onValueChange = { viewModel.additionalNotes.value = it },
                label = { Text("Uwagi dodatkowe") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    viewModel.generateNote(scenario)
                    onNavigateToPreview()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Generuj notatkę")
            }
        }
    }
}

@Composable
private fun UrgentProgressSection(progress: UrgentProgress) {
    val statusColor = when (progress.status) {
        UrgentStatus.READY_TO_CLOSE -> MaterialTheme.colorScheme.primary
        UrgentStatus.NEEDS_ATTENTION -> MaterialTheme.colorScheme.error
        UrgentStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
        if (progress.totalSteps > 0) {
            LinearProgressIndicator(
                progress = { progress.completedSteps.toFloat() / progress.totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }
    }
}

@Composable
private fun UrgentUncheckedCriticalSection(steps: List<ChecklistStepUi>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Niewykonane kroki krytyczne",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold
        )
        steps.forEach { step ->
            Text(
                text = "• ${step.text}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun UrgentChecklistRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    step: ChecklistStepUi
) {
    val textColor = if (step.isCritical) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val fontWeight = if (step.isCritical) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Column {
            Text(
                text = step.text,
                color = textColor,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.bodyLarge
            )
            if (step.isCritical) {
                Text(
                    text = "⚠ Krok krytyczny",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
