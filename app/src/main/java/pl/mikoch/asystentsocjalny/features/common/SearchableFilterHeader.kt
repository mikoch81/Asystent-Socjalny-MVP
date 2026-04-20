package pl.mikoch.asystentsocjalny.features.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val ALL_CATEGORIES = "Wszystkie"

/**
 * Reusable search bar + horizontal category filter chips.
 *
 * @param query free-text query
 * @param onQueryChange callback when query changes
 * @param categories list of category labels (without the "Wszystkie" head)
 * @param selectedCategory currently selected category, or [ALL_CATEGORIES] for no filter
 * @param onCategorySelected callback when a chip is selected
 * @param resultCount visible result count to display under the bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableFilterHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    resultCount: Int,
    placeholder: String = "Szukaj…",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Text("✕", style = MaterialTheme.typography.titleMedium)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val all = listOf(ALL_CATEGORIES) + categories
            all.forEach { cat ->
                FilterChip(
                    selected = cat == selectedCategory,
                    onClick = { onCategorySelected(cat) },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }
        Text(
            text = "Wyniki: $resultCount",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
