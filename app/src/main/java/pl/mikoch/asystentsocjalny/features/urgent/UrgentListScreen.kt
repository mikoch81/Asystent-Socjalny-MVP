package pl.mikoch.asystentsocjalny.features.urgent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi

@Composable
fun UrgentListScreen(
    scenarios: List<UrgentScenarioUi>,
    onOpenDetail: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Sytuacje pilne") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scenarios) { scenario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDetail(scenario.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = scenario.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
