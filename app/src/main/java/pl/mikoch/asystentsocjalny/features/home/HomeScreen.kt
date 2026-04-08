package pl.mikoch.asystentsocjalny.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onOpenProcedures: () -> Unit,
    onOpenBenefits: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenUrgent: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asystent Socjalny MVP") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Offline-first pomoc proceduralna dla pracy terenowej.",
                style = MaterialTheme.typography.bodyLarge
            )
            HomeCard(
                title = "Sytuacje pilne",
                description = "Scenariusze interwencyjne z checklistą krok po kroku.",
                buttonText = "Otwórz",
                onClick = onOpenUrgent
            )
            HomeCard(
                title = "Sytuacje pilne / Procedury",
                description = "Szybkie wejście do checklist i działań krok po kroku.",
                buttonText = "Otwórz",
                onClick = onOpenProcedures
            )
            HomeCard(
                title = "Świadczenia / Formy pomocy",
                description = "Katalog informacyjny z dokumentami i uwagami.",
                buttonText = "Otwórz",
                onClick = onOpenBenefits
            )
            HomeCard(
                title = "Generator notatki",
                description = "Szkic tekstu do dalszego uzupełnienia po interwencji.",
                buttonText = "Otwórz",
                onClick = onOpenNotes
            )
        }
    }
}

@Composable
private fun HomeCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = onClick,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}
