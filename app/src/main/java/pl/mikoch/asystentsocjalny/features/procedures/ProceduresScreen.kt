package pl.mikoch.asystentsocjalny.features.procedures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage

private val CATEGORY_ORDER = listOf(
    "Interwencyjne",
    "Zasiłki",
    "Niepełnosprawność",
    "Edukacja",
    "Rodzina i przemoc",
    "Seniorzy",
    "Bezdomność",
    "Inne formy pomocy"
)

@Composable
fun ProceduresScreen(
    procedures: List<Procedure>,
    onOpenDetail: (String) -> Unit
) {
    val grouped = remember(procedures) {
        val byCategory = procedures.groupBy { it.category.ifBlank { "Inne" } }
        CATEGORY_ORDER.mapNotNull { cat ->
            byCategory[cat]?.let { cat to it }
        } + byCategory.filterKeys { it !in CATEGORY_ORDER }.map { (cat, items) -> cat to items }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Procedury") }) }
    ) { innerPadding ->
        if (procedures.isEmpty()) {
            EmptyStateMessage(
                title = "Brak danych o procedurach",
                subtitle = "Nie udało się wczytać danych.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp)
            ) {
                grouped.forEach { (category, items) ->
                    stickyHeader(key = category) {
                        CategoryHeader(category)
                    }
                    items(items, key = { it.id }) { procedure ->
                        ProcedureCard(
                            procedure = procedure,
                            onClick = { onOpenDetail(procedure.id) },
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun ProcedureCard(
    procedure: Procedure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                    text = procedure.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                SeverityBadge(procedure.severity)
            }
            Text(
                text = procedure.situation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
