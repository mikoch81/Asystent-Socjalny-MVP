package pl.mikoch.asystentsocjalny.features.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.util.IntentLauncher
import pl.mikoch.asystentsocjalny.features.common.BaseScrollableScreen

private data class QuickContact(
    val name: String,
    val phone: String,
    val description: String,
    val emergency: Boolean = false
)

private val DEFAULT_CONTACTS = listOf(
    QuickContact("Numer alarmowy", "112", "Pomoc — policja, straż, pogotowie", emergency = true),
    QuickContact("Pogotowie ratunkowe", "999", "Zagrożenie życia, uraz, zatrucie", emergency = true),
    QuickContact("Policja", "997", "Przestępstwo, przemoc, zagrożenie", emergency = true),
    QuickContact("Straż pożarna", "998", "Pożar, ulatniający się gaz, awaria", emergency = true),
    QuickContact(
        "Niebieska Linia",
        "800120002",
        "Ofiary przemocy w rodzinie — czynna 24/7, bezpłatna"
    ),
    QuickContact(
        "Telefon Zaufania dla Dzieci",
        "116111",
        "Dzieci i młodzież w kryzysie — czynna 24/7"
    ),
    QuickContact(
        "Telefon Zaufania dla Dorosłych",
        "116123",
        "Wsparcie psychologiczne — czynna 14:00-22:00"
    ),
    QuickContact(
        "Centrum Wsparcia (kryzys psychiczny)",
        "800702222",
        "Pomoc w kryzysie psychicznym 24/7"
    )
)

@Composable
fun QuickContactsScreen() {
    val context = LocalContext.current
    BaseScrollableScreen(title = "Szybkie kontakty") {
        item("intro") {
            Text(
                text = "Numery dostępne offline. Naciśnij, by przejść do telefonu — " +
                    "zatwierdź połączenie ręcznie.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(DEFAULT_CONTACTS) { contact ->
            QuickContactCard(contact) {
                IntentLauncher.dialPhone(context, contact.phone)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    list: List<QuickContact>,
    itemContent: @Composable (QuickContact) -> Unit
) {
    list.forEach { c ->
        item(key = c.phone) { itemContent(c) }
    }
}

@Composable
private fun QuickContactCard(contact: QuickContact, onClick: () -> Unit) {
    val container = if (contact.emergency) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val onContainer = if (contact.emergency) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onContainer
            )
            Text(
                text = contact.description,
                style = MaterialTheme.typography.bodySmall,
                color = onContainer
            )
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = if (contact.emergency) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text("☎ Zadzwoń ${contact.phone}")
            }
        }
    }
}
