package pl.mikoch.asystentsocjalny.features.benefits

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Benefit
import pl.mikoch.asystentsocjalny.features.common.ALL_CATEGORIES
import pl.mikoch.asystentsocjalny.features.common.EmptyStateMessage
import pl.mikoch.asystentsocjalny.features.common.SearchableFilterHeader

private val BENEFIT_CATEGORY_ORDER = listOf(
    "Zasiłki",
    "Niepełnosprawność",
    "Edukacja",
    "Rodzina i przemoc",
    "Seniorzy",
    "Bezdomność",
    "Inne formy pomocy"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BenefitsScreen(
    benefits: List<Benefit>,
    onOpenDetail: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ALL_CATEGORIES) }

    val availableCategories = remember(benefits) {
        val present = benefits.map { it.category.ifBlank { "Inne" } }.toSet()
        BENEFIT_CATEGORY_ORDER.filter { it in present } +
            present.filter { it !in BENEFIT_CATEGORY_ORDER }.sorted()
    }

    val filtered = remember(benefits, query, selectedCategory) {
        val q = query.trim().lowercase()
        benefits.filter { b ->
            val matchesCategory = selectedCategory == ALL_CATEGORIES ||
                b.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = q.isEmpty() ||
                b.name.lowercase().contains(q) ||
                b.description.lowercase().contains(q) ||
                b.category.lowercase().contains(q) ||
                b.documents.any { it.lowercase().contains(q) } ||
                b.conditions.any { it.lowercase().contains(q) }
            matchesCategory && matchesQuery
        }
    }

    val grouped = remember(filtered) {
        val byCategory = filtered.groupBy { it.category.ifBlank { "Inne" } }
        BENEFIT_CATEGORY_ORDER.mapNotNull { category ->
            byCategory[category]?.let { category to it }
        } + byCategory.filterKeys { it !in BENEFIT_CATEGORY_ORDER }
            .map { (category, items) -> category to items }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Świadczenia i formy pomocy") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchableFilterHeader(
                query = query,
                onQueryChange = { query = it },
                categories = availableCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                resultCount = filtered.size,
                placeholder = "Szukaj świadczenia…"
            )
            when {
                benefits.isEmpty() -> EmptyStateMessage(
                    icon = "📁",
                    title = "Brak danych o świadczeniach",
                    subtitle = "Nie udało się wczytać danych z paczki offline."
                )
                filtered.isEmpty() -> EmptyStateMessage(
                    icon = "🔍",
                    title = "Nic nie pasuje do „$query”",
                    subtitle = "Spróbuj krótszej frazy lub innej kategorii.",
                    actionLabel = "Wyczyść filtry",
                    onAction = {
                        query = ""
                        selectedCategory = pl.mikoch.asystentsocjalny.features.common.ALL_CATEGORIES
                    }
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    grouped.forEach { (category, items) ->
                        stickyHeader(key = category) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(items, key = { it.id }) { benefit ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenDetail(benefit.id) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = benefit.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = benefit.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
