package pl.mikoch.asystentsocjalny.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.mikoch.asystentsocjalny.core.model.TextScale
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen

@Composable
fun SettingsScreen(
    onSaved: () -> Unit = {},
    onOpenChangelog: () -> Unit = {},
    viewModel: WorkerProfileViewModel = hiltViewModel()
) {
    val saved by viewModel.profileFlow.collectAsState(initial = WorkerProfile.EMPTY)

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
                        email = email,
                        textScale = saved.textScale,
                        highContrast = saved.highContrast
                    )
                    viewModel.save(profile) {
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
        item("a11y_header") {
            Text(
                text = "Dostępność",
                style = MaterialTheme.typography.titleMedium
            )
        }
        item("a11y_desc") {
            Text(
                text = "Dostosuj wielkość tekstu i kontrast — pomoże w słabym świetle\n" +
                    "i przy długich rozmowach z klientem.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item("a11y_scale") {
            Text(
                text = "Wielkość tekstu",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Wielkość tekstu" },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextScale.entries.forEach { scale ->
                    FilterChip(
                        selected = scale == saved.textScale,
                        onClick = { viewModel.setTextScale(scale) },
                        label = { Text(scale.label) },
                        modifier = Modifier.semantics {
                            contentDescription = when (scale) {
                                TextScale.SMALL -> "Mała"
                                TextScale.MEDIUM -> "Średnia"
                                TextScale.LARGE -> "Duża"
                                TextScale.EXTRA_LARGE -> "Bardzo duża"
                            }
                        }
                    )
                }
            }
        }
        item("a11y_contrast") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Wysoki kontrast",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = saved.highContrast,
                    onCheckedChange = { viewModel.setHighContrast(it) },
                    modifier = Modifier.semantics {
                        contentDescription = "Wysoki kontrast — przełącznik"
                    }
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
