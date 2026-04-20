package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import pl.mikoch.asystentsocjalny.features.notes.buildNoteDraft

class NoteDraftBuilderProfileTest {

    private val procedure = Procedure(
        id = "p1",
        title = "Procedura X",
        category = "Interwencyjne",
        situation = "Sytuacja",
        severity = "Wysoki",
        nowSteps = listOf("Krok"),
        notify = listOf("Policja"),
        doNotMiss = emptyList(),
        legalBasis = emptyList(),
        escalation = "OK",
        documents = emptyList()
    )

    @Test
    fun draftWithCompleteProfile_includesSignatureLine() {
        val worker = WorkerProfile(
            firstName = "Anna",
            lastName = "Kowalska",
            position = "Pracownik socjalny",
            unit = "MOPS Zgierz",
            phone = "42 716 42 13"
        )
        val out = buildNoteDraft(procedure, worker, location = "ul. Piłsudskiego 12")

        assertTrue("worker name expected", out.contains("Anna Kowalska"))
        assertTrue("position expected", out.contains("Pracownik socjalny"))
        assertTrue("phone line expected", out.contains("Telefon służbowy: 42 716 42 13"))
        assertTrue("location expected", out.contains("Miejsce: ul. Piłsudskiego 12"))
        assertFalse("placeholder must be replaced", out.contains("Pracownik: [uzupełnij]"))
    }

    @Test
    fun draftWithEmptyProfile_keepsPlaceholders() {
        val out = buildNoteDraft(procedure, WorkerProfile.EMPTY, location = "")
        assertTrue(out.contains("Pracownik: [uzupełnij]"))
        assertTrue(out.contains("Miejsce: [uzupełnij]"))
        assertFalse(out.contains("Telefon służbowy:"))
    }

    @Test
    fun draftWithLocationOnly_replacesLocationPlaceholder() {
        val out = buildNoteDraft(procedure, WorkerProfile.EMPTY, location = "Zgierz, Rynek 1")
        assertTrue(out.contains("Miejsce: Zgierz, Rynek 1"))
        assertTrue(out.contains("Pracownik: [uzupełnij]"))
    }
}
