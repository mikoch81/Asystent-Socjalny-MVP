package pl.mikoch.asystentsocjalny.features.benefits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Benefit

@Composable
fun BenefitDetailScreen(benefit: Benefit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(benefit.name) }) }
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
                text = benefit.description,
                style = MaterialTheme.typography.bodyLarge
            )

            BenefitSection("Wymagane dokumenty", benefit.documents)
            BenefitSection("Warunki ogólne", benefit.conditions)

            Section("Uwagi") {
                Text(
                    text = benefit.note,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BenefitSection(title: String, items: List<String>) {
    if (items.isEmpty()) return
    Section(title) {
        items.forEach { item ->
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}
