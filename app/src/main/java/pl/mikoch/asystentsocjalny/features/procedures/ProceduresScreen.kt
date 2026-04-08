package pl.mikoch.asystentsocjalny.features.procedures

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Procedure

@Composable
fun ProceduresScreen(
    procedures: List<Procedure>,
    onOpenDetail: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Procedury pilne") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(procedures) { procedure ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDetail(procedure.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(procedure.title, style = MaterialTheme.typography.titleMedium)
                        Text(procedure.situation, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Priorytet: ${procedure.severity}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
