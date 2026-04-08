package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.features.notes.buildNoteDraft

class NoteDraftBuilderTest {

    @Test
    fun buildNoteDraft_containsProcedureSections() {
        val procedure = Procedure(
            id = "p1",
            title = "Testowa procedura",
            category = "Interwencyjne",
            situation = "Sytuacja testowa",
            severity = "Wysoki",
            nowSteps = listOf("Krok 1", "Krok 2"),
            notify = listOf("Policja"),
            doNotMiss = listOf("Nic nie pomijaj"),
            legalBasis = listOf("Źródło"),
            escalation = "Wymagana konsultacja",
            documents = listOf("Notatka")
        )

        val result = buildNoteDraft(procedure)

        assertTrue(result.contains("Testowa procedura"))
        assertTrue(result.contains("Krok 1"))
        assertTrue(result.contains("Policja"))
        assertTrue(result.contains("Notatka"))
    }
}
