package pl.mikoch.asystentsocjalny.features.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen

@Composable
fun SettingsScreen(
    onSaved: () -> Unit = {},
    onOpenChangelog: () -> Unit = {}
) {
    val context = LocalContext.current
    val store = remember { WorkerProfileStore(context) }
    val saved by store.profileFlow.collectAsState(initial = WorkerProfile.EMPTY)
    val scope = rememberCoroutineScope()

    var firstName by remember(saved) { mutableStateOf(saved.firstName) }
    var lastName by remember(saved) { mutableStateOf(saved.lastName) }
    var position by remember(saved) { mutableStateOf(saved.position) }
    var unit by remember(saved) { mutableStateOf(saved.unit) }
    var phone by remember(saved) { mutableStateOf(saved.phone) }
    var email by remember(saved) { mutableStateOf(saved.email) }
    var savedToast by remember { mutableStateOf(false) }

    BaseScrollableScreen(title = "Profil pracownika") {
        item("intro") {
            Text(
                text = "Dane wprowadzone tutaj są zapisywane lokalnie na urządzeniu " +
                    "i automatycznie wstawiane do generowanych notatek i PDF.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item("first") {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Imię *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("last") {
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nazwisko *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("pos") {
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Stanowisko") },
                placeholder = { Text("np. Pracownik socjalny") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("unit") {
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Jednostka") },
                placeholder = { Text("np. MOPS Zgierz") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("phone") {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefon służbowy") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("email") {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail służbowy") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item("hint") {
            Text(
                text = "* pola wymagane do podpisania notatki",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item("save") {
            Button(
                onClick = {
                    val profile = WorkerProfile(
                        firstName = firstName,
                        lastName = lastName,
                        position = position,
                        unit = unit,
                        phone = phone,
                        email = email
                    )
                    scope.launch {
                        store.save(profile)
                        savedToast = true
                        onSaved()
                    }
                },
                enabled = firstName.isNotBlank() && lastName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz profil")
            }
        }
        if (savedToast) {
            item("confirm") {
                Text(
                    text = "✓ Zapisano. Profil będzie podpisywać kolejne notatki.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        item("changelog") {
            OutlinedButton(
                onClick = onOpenChangelog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📜  Co nowego (changelog)")
            }
        }
    }
}
