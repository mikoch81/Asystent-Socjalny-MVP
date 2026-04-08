package pl.mikoch.asystentsocjalny.features.urgent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.ActionRecommendation
import pl.mikoch.asystentsocjalny.core.model.RecommendationPriority
import pl.mikoch.asystentsocjalny.core.model.RiskAssessment
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.core.model.SituationFlag
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen
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
    onNavigateToSummary: () -> Unit,
    caseId: String? = null
) {
    LaunchedEffect(scenario.id, caseId) {
        viewModel.initDetailState(scenario, caseId)
    }

    DisposableEffect(scenario.id) {
        onDispose { viewModel.saveDraft() }
    }

    val progress by viewModel.progress
    val riskAssessment by viewModel.riskAssessment
    val recommendation by viewModel.recommendation
    val noteSuggestions by viewModel.noteSuggestions
    val draftRestored by viewModel.draftRestored

    BaseScrollableScreen(
        title = scenario.title,
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = {
                        viewModel.generateNote(scenario)
                        onNavigateToPreview()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(56.dp)
                ) {
                    Text("Generuj notatkę służbową")
                }
            }
        }
    ) {
        if (draftRestored) {
            item(key = "draft_hint") {
                DraftRestoredHint(
                    onDismiss = { viewModel.dismissDraftHint() },
                    onClear = { viewModel.clearDraft() }
                )
            }
        }

        item(key = "description") {
            Text(
                text = scenario.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        stickyHeader(key = "progress") {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                UrgentProgressSection(
                    progress = progress,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        item(key = "risk") { RiskAssessmentSection(riskAssessment) }
        item(key = "recommendation") { ActionRecommendationSection(recommendation) }

        if (progress.uncheckedCriticalSteps.isNotEmpty()) {
            item(key = "unchecked") {
                UrgentUncheckedCriticalSection(progress.uncheckedCriticalSteps)
            }
        }

        scenario.guidance?.let { guidance ->
            item(key = "guidance") { UrgentGuidanceSection(guidance) }
        }

        item(key = "checklist_header") {
            Text(
                text = "Lista kontrolna",
                style = MaterialTheme.typography.titleMedium
            )
        }

        itemsIndexed(
            items = scenario.steps,
            key = { index, _ -> "step_$index" }
        ) { index, step ->
            UrgentChecklistRow(
                checked = viewModel.checkedStates.getOrElse(index) { false },
                onCheckedChange = { viewModel.checkedStates[index] = it },
                step = step,
                stepNote = viewModel.stepNotes[index] ?: "",
                onStepNoteChange = { note ->
                    if (note.isBlank()) viewModel.stepNotes.remove(index)
                    else viewModel.stepNotes[index] = note
                }
            )
        }

        item(key = "situation_flags") {
            SituationFlagsSection(
                selectedFlags = viewModel.situationFlags,
                onToggle = { flag ->
                    if (flag in viewModel.situationFlags) viewModel.situationFlags.remove(flag)
                    else viewModel.situationFlags.add(flag)
                }
            )
        }

        item(key = "persons_present") {
            OutlinedTextField(
                value = viewModel.personsPresent.value,
                onValueChange = { viewModel.personsPresent.value = it },
                label = { Text("Osoby obecne") },
                placeholder = { Text("np. matka, dziecko, sąsiad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item(key = "running_notes") {
            OutlinedTextField(
                value = viewModel.runningNotes.value,
                onValueChange = { viewModel.runningNotes.value = it },
                label = { Text("Notatki bieżące") },
                placeholder = { Text("Zapisuj obserwacje w trakcie interwencji") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        if (noteSuggestions.isNotEmpty()) {
            item(key = "suggestions") {
                NoteSuggestionsSection(
                    suggestions = noteSuggestions,
                    onSuggestionClick = { suggestion ->
                        val current = viewModel.additionalNotes.value
                        if (!current.contains(suggestion)) {
                            viewModel.additionalNotes.value =
                                if (current.isBlank()) suggestion
                                else "$current\n$suggestion"
                        }
                    },
                    onAddAll = {
                        val current = viewModel.additionalNotes.value
                        val newLines = noteSuggestions.filter { it !in current }
                        if (newLines.isNotEmpty()) {
                            viewModel.additionalNotes.value =
                                if (current.isBlank()) newLines.joinToString("\n")
                                else current + "\n" + newLines.joinToString("\n")
                        }
                    }
                )
            }
        }

        item(key = "input_fields") {
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Dane do notatki", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = viewModel.location.value,
                    onValueChange = { viewModel.location.value = it },
                    label = { Text("Miejsce interwencji") },
                    placeholder = { Text("np. ul. Przykładowa 5, Warszawa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                OutlinedTextField(
                    value = viewModel.situationDescription.value,
                    onValueChange = { viewModel.situationDescription.value = it },
                    label = { Text("Opis sytuacji") },
                    placeholder = { Text("Krótki opis zastanej sytuacji") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                OutlinedTextField(
                    value = viewModel.additionalNotes.value,
                    onValueChange = { viewModel.additionalNotes.value = it },
                    label = { Text("Uwagi dodatkowe") },
                    placeholder = { Text("Opcjonalne uwagi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
            }
        }

        item(key = "btn_summary") {
            OutlinedButton(
                onClick = {
                    viewModel.saveDraft()
                    onNavigateToSummary()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zobacz podsumowanie")
            }
        }

        item(key = "btn_clear") {
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
private fun UrgentProgressSection(progress: UrgentProgress, modifier: Modifier = Modifier) {
    val statusColor = when (progress.status) {
        UrgentStatus.READY_TO_CLOSE -> MaterialTheme.colorScheme.primary
        UrgentStatus.NEEDS_ATTENTION -> MaterialTheme.colorScheme.error
        UrgentStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
    step: ChecklistStepUi,
    stepNote: String = "",
    onStepNoteChange: (String) -> Unit = {}
) {
    var noteExpanded by remember { mutableStateOf(stepNote.isNotBlank()) }

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
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
            TextButton(onClick = { noteExpanded = !noteExpanded }) {
                Text(
                    if (noteExpanded) "−" else "+",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        AnimatedVisibility(visible = noteExpanded) {
            OutlinedTextField(
                value = stepNote,
                onValueChange = onStepNoteChange,
                placeholder = { Text("Notatka do kroku") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 8.dp, bottom = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )
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
            text = "Co dalej",
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

@Composable
private fun ActionRecommendationSection(recommendation: ActionRecommendation) {
    val (bgColor, contentColor, badgeColor, icon) = when (recommendation.priority) {
        RecommendationPriority.HIGH -> RecommendationColors(
            Color(0xFFFCE4EC),
            Color(0xFFC62828),
            Color(0xFFFFCDD2),
            "🚨"
        )
        RecommendationPriority.MEDIUM -> RecommendationColors(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            Color(0xFFFFE0B2),
            "📌"
        )
        RecommendationPriority.LOW -> RecommendationColors(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Color(0xFFC8E6C9),
            "✅"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$icon  ${recommendation.title}",
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = recommendation.priority.label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        Text(
            text = recommendation.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )

        if (recommendation.actions.isNotEmpty()) {
            Text(
                text = "Zalecane działania:",
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
            recommendation.actions.forEach { action ->
                Text(
                    text = "  •  $action",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }

        if (recommendation.warnings.isNotEmpty()) {
            Text(
                text = "⚠  Uwaga:",
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
            recommendation.warnings.forEach { warning ->
                Text(
                    text = "  •  $warning",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}

private data class RecommendationColors(
    val bgColor: Color,
    val contentColor: Color,
    val badgeColor: Color,
    val icon: String
)

@Composable
private fun SituationFlagsSection(
    selectedFlags: List<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Stan sytuacji", style = MaterialTheme.typography.titleMedium)
        SituationFlag.ALL.forEach { flag ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = flag in selectedFlags,
                    onCheckedChange = { onToggle(flag) }
                )
                Text(flag, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun NoteSuggestionsSection(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onAddAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sugestie do notatki",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        suggestions.forEach { suggestion ->
            Text(
                text = "•  $suggestion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }
        OutlinedButton(
            onClick = onAddAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dodaj wszystkie sugestie")
        }
    }
}
