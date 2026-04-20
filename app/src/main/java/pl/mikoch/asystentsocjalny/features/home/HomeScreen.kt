package pl.mikoch.asystentsocjalny.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenProcedures: () -> Unit,
    onOpenBenefits: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenUrgent: () -> Unit,
    onOpenCases: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenContacts: () -> Unit = {}
) {
    val context = LocalContext.current
    val store = remember { WorkerProfileStore(context) }
    val profile by store.profileFlow.collectAsState(initial = WorkerProfile.EMPTY)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mobile Social Shield") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Text(text = "⚙", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Wsparcie proceduralne offline",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!profile.isComplete) {
                HomeCard(
                    title = "⚠️  Uzupełnij profil pracownika",
                    description = "Bez profilu notatki i PDF są podpisywane jako [imię i nazwisko].",
                    buttonText = "Otwórz ustawienia",
                    onClick = onOpenSettings,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            } else {
                HomeCard(
                    title = "👤  ${profile.fullName}",
                    description = listOf(profile.position, profile.unit)
                        .filter { it.isNotBlank() }.joinToString(" • ")
                        .ifBlank { "Profil pracownika" },
                    buttonText = "Edytuj",
                    onClick = onOpenSettings
                )
            }
            HomeCard(
                title = "🔴  Sytuacje pilne",
                description = "Interwencja krok po kroku",
                buttonText = "Rozpocznij",
                onClick = onOpenUrgent,
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
            HomeCard(
                title = "Procedury",
                description = "Katalog działań i procedur",
                buttonText = "Zobacz",
                onClick = onOpenProcedures
            )
            HomeCard(
                title = "Świadczenia",
                description = "Formy pomocy i dokumenty",
                buttonText = "Zobacz",
                onClick = onOpenBenefits
            )
            HomeCard(
                title = "Notatki",
                description = "Szkic notatki po interwencji",
                buttonText = "Utwórz",
                onClick = onOpenNotes
            )
            HomeCard(
                title = "☎  Szybkie kontakty",
                description = "Numery alarmowe i wsparcia (offline)",
                buttonText = "Otwórz",
                onClick = onOpenContacts
            )
            HomeCard(
                title = "📋  Sprawy",
                description = "Lista prowadzonych spraw",
                buttonText = "Otwórz",
                onClick = onOpenCases
            )
        }
    }
}

@Composable
private fun HomeCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onClick,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}
