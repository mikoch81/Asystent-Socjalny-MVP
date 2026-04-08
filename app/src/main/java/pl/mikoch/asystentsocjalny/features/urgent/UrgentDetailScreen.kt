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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.RiskAssessment
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi
import pl.mikoch.asystentsocjalny.features.urgent.model.GuidanceUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus

@Composable
fun UrgentDetailScreen(
    scenario: UrgentScenarioUi,
    viewModel: UrgentViewModel,
    onNavigateToPreview: () -> Unit,
    onNavigateToSummary: () -> Unit
) {
    LaunchedEffect(scenario.id) {
        viewModel.initDetailState(scenario)
    }

    DisposableEffect(scenario.id) {
        onDispose { viewModel.saveDraft() }
    }

    val progress by viewModel.progress
    val riskAssessment by viewModel.riskAssessment
    val draftRestored by viewModel.draftRestored

    Scaffold(
        topBar = { TopAppBar(title = { Text(scenario.title) }) }
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
            // --- Draft restored hint ---
            if (draftRestored) {
                DraftRestoredHint(
                    onDismiss = { viewModel.dismissDraftHint() },
                    onClear = { viewModel.clearDraft() }
                )
            }

            Text(
                text = scenario.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // --- Progress & Status ---
            UrgentProgressSection(progress)

            // --- Risk Assessment ---
            RiskAssessmentSection(riskAssessment)

            // --- Unchecked critical steps ---
            if (progress.uncheckedCriticalSteps.isNotEmpty()) {
                UrgentUncheckedCriticalSection(progress.uncheckedCriticalSteps)
            }

            // --- What next guidance ---
            scenario.guidance?.let { guidance ->
                UrgentGuidanceSection(guidance)
            }

            Text(
                text = "Lista kontrolna",
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
                placeholder = { Text("np. ul. Przykładowa 5, Warszawa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = viewModel.situationDescription.value,
                onValueChange = { viewModel.situationDescription.value = it },
                label = { Text("Opis sytuacji") },
                placeholder = { Text("Krótki opis zastanej sytuacji") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = viewModel.additionalNotes.value,
                onValueChange = { viewModel.additionalNotes.value = it },
                label = { Text("Uwagi dodatkowe") },
                placeholder = { Text("Opcjonalne uwagi") },
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
                Text("Generuj notatkę służbową")
            }

            OutlinedButton(
                onClick = {
                    viewModel.saveDraft()
                    onNavigateToSummary()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zobacz podsumowanie")
            }

            OutlinedButton(
                onClick = { viewModel.clearDraft() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wyczyść zapis roboczy")
            }
        }
    }
}

@Composable
private fun DraftRestoredHint(onDismiss: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Przywrócono zapis roboczy",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onClear) {
            Text("Wyczyść", color = MaterialTheme.colorScheme.error)
        }
        TextButton(onClick = onDismiss) {
            Text("OK")
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "⚠  Niewykonane kroki krytyczne",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold
        )
        steps.forEach { step ->
            Text(
                text = "  ✗  ${step.text}",
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
    val bgColor = if (step.isCritical && !checked) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    } else {
        androidx.compose.ui.graphics.Color.Transparent
    }
    val textColor = if (step.isCritical) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val fontWeight = if (step.isCritical) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.weight(1f)) {
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

@Composable
private fun UrgentGuidanceSection(guidance: GuidanceUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "📋  Co dalej",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )

        if (guidance.notify.isNotEmpty()) {
            GuidanceBulletList(
                header = "Kogo powiadomić",
                items = guidance.notify
            )
        }

        if (guidance.doNotMiss.isNotEmpty()) {
            GuidanceBulletList(
                header = "Czego nie pominąć",
                items = guidance.doNotMiss
            )
        }

        if (guidance.documents.isNotEmpty()) {
            GuidanceBulletList(
                header = "Dokumenty do przygotowania",
                items = guidance.documents
            )
        }

        val escalationColor = if (guidance.escalationRequired) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }
        Text(
            text = if (guidance.escalationRequired) "⚠  Wymagana eskalacja" else "Eskalacja niewymagana",
            style = MaterialTheme.typography.titleSmall,
            color = escalationColor,
            fontWeight = FontWeight.Bold
        )
        if (guidance.escalationNote.isNotEmpty()) {
            Text(
                text = guidance.escalationNote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun GuidanceBulletList(header: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            Text(
                text = "  •  $item",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun RiskAssessmentSection(assessment: RiskAssessment) {
    val (bgColor, contentColor, icon) = when (assessment.level) {
        RiskLevel.HIGH -> Triple(
            Color(0xFFFFCDD2),
            Color(0xFFC62828),
            "🔴"
        )
        RiskLevel.MEDIUM -> Triple(
            Color(0xFFFFE0B2),
            Color(0xFFE65100),
            "🟠"
        )
        RiskLevel.LOW -> Triple(
            Color(0xFFC8E6C9),
            Color(0xFF2E7D32),
            "🟢"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$icon  Ryzyko: ${assessment.level.label}",
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
        assessment.reasons.forEach { reason ->
            Text(
                text = "•  $reason",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}
