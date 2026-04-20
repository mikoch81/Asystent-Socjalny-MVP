package pl.mikoch.asystentsocjalny.features.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.data.CaseLifecycleRules
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaseListScreen(
    viewModel: CaseListViewModel,
    onOpenCase: (caseId: String, scenarioId: String) -> Unit,
    onNewCase: () -> Unit,
    onOpenDocuments: (caseId: String) -> Unit = {}
) {
    LaunchedEffect(Unit) { viewModel.loadCases() }

    val cases by viewModel.cases
    val currentFilter by viewModel.lifecycleFilter

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sprawy") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onNewCase,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Nowa sprawa")
            }

            LifecycleFilterRow(
                selected = currentFilter,
                onSelected = { viewModel.setFilter(it) }
            )

            if (cases.isEmpty()) {
                EmptyStateMessage(
                    title = "Brak zapisanych spraw",
                    subtitle = "Utwórz nową sprawę, aby rozpocząć."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cases, key = { it.caseId }) { record ->
                        val displayLc = CaseLifecycleRules.displayLifecycle(record)
                        CaseCard(
                            record = record,
                            displayLifecycle = displayLc,
                            onClick = {
                                if (CaseLifecycleRules.canEdit(record)) {
                                    onOpenCase(record.caseId, record.scenarioId)
                                }
                            },
                            onDelete = { viewModel.deleteCase(record.caseId) },
                            onOpenDocuments = { onOpenDocuments(record.caseId) },
                            onClose = { viewModel.closeCase(record.caseId) },
                            onArchive = { viewModel.archiveCase(record.caseId) },
                            onRestore = { viewModel.restoreCase(record.caseId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LifecycleFilterRow(
    selected: CaseLifecycle?,
    onSelected: (CaseLifecycle?) -> Unit
) {
    data class FilterOption(val lifecycle: CaseLifecycle?, val label: String)

    val options = listOf(
        FilterOption(null, "Wszystkie"),
        FilterOption(CaseLifecycle.ACTIVE, "Aktywne"),
        FilterOption(CaseLifecycle.CLOSED, "Zamknięte"),
        FilterOption(CaseLifecycle.ARCHIVED, "Archiwum")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt.lifecycle,
                onClick = { onSelected(opt.lifecycle) },
                label = { Text(opt.label) }
            )
        }
    }
}

@Composable
private fun CaseCard(
    record: CaseRecord,
    displayLifecycle: CaseLifecycle,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onOpenDocuments: () -> Unit,
    onClose: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    text = record.scenarioTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                RiskBadge(record.riskLevel)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusLabel(record.status)
                LifecycleBadge(displayLifecycle)
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                Text(
                    text = formatTimestamp(record.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (record.locationPreview.isNotBlank()) {
                Text(
                    text = record.locationPreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onOpenDocuments) {
                    Text("Dokumenty")
                }
                if (CaseLifecycleRules.canClose(record)) {
                    TextButton(onClick = onClose) {
                        Text("Zamknij")
                    }
                }
                if (CaseLifecycleRules.canArchive(record)) {
                    TextButton(onClick = onArchive) {
                        Text("Archiwizuj")
                    }
                }
                if (CaseLifecycleRules.canRestore(record)) {
                    TextButton(onClick = onRestore) {
                        Text("Przywróć")
                    }
                }
                TextButton(onClick = onDelete) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun StatusLabel(status: CaseStatus) {
    val (bg, fg) = when (status) {
        CaseStatus.DRAFT -> Pair(Color(0xFFE0E0E0), Color(0xFF616161))
        CaseStatus.IN_PROGRESS -> Pair(Color(0xFFBBDEFB), Color(0xFF1565C0))
        CaseStatus.READY_TO_CLOSE -> Pair(Color(0xFFC8E6C9), Color(0xFF2E7D32))
    }
    Text(
        text = status.label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun LifecycleBadge(lifecycle: CaseLifecycle) {
    val (bg, fg) = when (lifecycle) {
        CaseLifecycle.ACTIVE -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        CaseLifecycle.READY_TO_CLOSE -> Pair(Color(0xFFFFF9C4), Color(0xFFF57F17))
        CaseLifecycle.CLOSED -> Pair(Color(0xFFE0E0E0), Color(0xFF424242))
        CaseLifecycle.ARCHIVED -> Pair(Color(0xFFEFEBE9), Color(0xFF6D4C41))
    }
    Text(
        text = lifecycle.label,
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun RiskBadge(level: RiskLevel) {
    val (bg, fg, icon) = when (level) {
        RiskLevel.HIGH -> Triple(Color(0xFFFFCDD2), Color(0xFFC62828), "🔴")
        RiskLevel.MEDIUM -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100), "🟠")
        RiskLevel.LOW -> Triple(Color(0xFFC8E6C9), Color(0xFF2E7D32), "🟢")
    }
    Text(
        text = "$icon ${level.label}",
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

private fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.forLanguageTag("pl-PL"))
    return sdf.format(Date(millis))
}
