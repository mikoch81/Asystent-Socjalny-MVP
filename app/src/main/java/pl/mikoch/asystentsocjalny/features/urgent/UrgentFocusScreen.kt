package pl.mikoch.asystentsocjalny.features.urgent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi

/**
 * "Tryb przy kliencie" — uproszczony, pełnoekranowy widok jednej czynności
 * z listy kontrolnej. Duża czcionka, duże dotykalne przyciski, brak rozpraszaczy.
 *
 * Stan kroków (checkedStates) współdzielony z UrgentDetailScreen przez aktywno-skopowany
 * UrgentViewModel (ten sam instance jest przekazywany z NavHost).
 */
@Composable
fun UrgentFocusScreen(
    scenario: UrgentScenarioUi,
    viewModel: UrgentViewModel,
    onExit: () -> Unit
) {
    // Inicjalizacja stanu jeśli jeszcze nie była zrobiona w detail-screen
    LaunchedEffect(scenario.id) {
        if (viewModel.checkedStates.size != scenario.steps.size) {
            viewModel.initDetailState(scenario, caseId = null)
        }
    }

    val steps = scenario.steps
    val total = steps.size
    var currentIndex by remember { mutableIntStateOf(firstUncheckedIndex(viewModel.checkedStates, total)) }
    val safeIndex = currentIndex.coerceIn(0, (total - 1).coerceAtLeast(0))
    val step = steps.getOrNull(safeIndex)

    val completedCount by remember {
        derivedStateOf { viewModel.checkedStates.count { it } }
    }

    BackHandler { onExit() }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pasek górny: tytuł + wyjście
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = scenario.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = onExit,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .semantics { contentDescription = "Wyjdź z trybu przy kliencie" }
                ) {
                    Text("Wyjdź")
                }
            }

            // Pasek postępu (czytelny, bez procentów)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Krok ${safeIndex + 1} z $total  •  ukończonych $completedCount",
                    style = MaterialTheme.typography.titleMedium
                )
                LinearProgressIndicator(
                    progress = { if (total == 0) 0f else (safeIndex + 1f) / total },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }

            if (step == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Brak kroków w scenariuszu.",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                return@Column
            }

            // Karta z aktualnym krokiem — duży tekst
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = if (step.isCritical) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (step.isCritical) {
                        Text(
                            text = "⚠️  KROK KRYTYCZNY",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = step.text,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (step.isCritical) MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onSurface
                    )
                    val checked = viewModel.checkedStates.getOrElse(safeIndex) { false }
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (safeIndex in viewModel.checkedStates.indices) {
                                viewModel.checkedStates[safeIndex] = !checked
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp),
                        colors = if (checked) ButtonDefaults.outlinedButtonColors()
                                 else ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            text = if (checked) "✓ Wykonano (cofnij)" else "Oznacz jako wykonane",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Nawigacja krok ↔ krok — duże przyciski
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { if (safeIndex > 0) currentIndex = safeIndex - 1 },
                    enabled = safeIndex > 0,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                        .semantics { contentDescription = "Poprzedni krok" }
                ) {
                    Text("← Poprzedni", style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = { if (safeIndex < total - 1) currentIndex = safeIndex + 1 },
                    enabled = safeIndex < total - 1,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                        .semantics { contentDescription = "Następny krok" }
                ) {
                    Text("Następny →", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

private fun firstUncheckedIndex(states: SnapshotStateList<Boolean>, total: Int): Int {
    if (total == 0) return 0
    val idx = states.indexOfFirst { !it }
    return if (idx >= 0) idx else 0
}
