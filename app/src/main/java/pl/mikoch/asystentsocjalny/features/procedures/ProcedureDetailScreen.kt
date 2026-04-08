package pl.mikoch.asystentsocjalny.features.procedures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.mikoch.asystentsocjalny.core.model.Procedure

@Composable
fun ProcedureDetailScreen(procedure: Procedure) {
    val checkedStates = remember(procedure.id) {
        mutableStateListOf<Boolean>().apply {
            repeat(procedure.nowSteps.size) { add(false) }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(procedure.title) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Section("Co zrobić teraz") {
                procedure.nowSteps.forEachIndexed { index, step ->
                    ChecklistRow(
                        checked = checkedStates[index],
                        onCheckedChange = { checkedStates[index] = it },
                        text = step
                    )
                }
            }
            Section("Kogo powiadomić") {
                procedure.notify.forEach { Text("• $it") }
            }
            Section("Czego nie pominąć") {
                procedure.doNotMiss.forEach { Text("• $it") }
            }
            Section("Podstawa prawna / źródło") {
                procedure.legalBasis.forEach { Text("• $it") }
            }
            Section("Czy wymagana konsultacja") {
                Text(procedure.escalation)
            }
            Section("Jakie dokumenty przygotować") {
                procedure.documents.forEach { Text("• $it") }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun ChecklistRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text, modifier = Modifier.padding(top = 12.dp))
    }
}
