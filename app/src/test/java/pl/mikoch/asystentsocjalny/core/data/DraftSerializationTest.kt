package pl.mikoch.asystentsocjalny.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DraftSerializationTest {

    private fun sampleDraft(
        scenarioId: String = "test_scenario",
        checkedStates: List<Boolean> = listOf(true, false, true),
        location: String = "Warszawa",
        situationDescription: String = "Opis sytuacji",
        additionalNotes: String = "Dodatkowe uwagi",
        generatedNoteText: String = "Treść notatki"
    ) = UrgentDraft(
        scenarioId = scenarioId,
        checkedStates = checkedStates,
        location = location,
        situationDescription = situationDescription,
        additionalNotes = additionalNotes,
        generatedNoteText = generatedNoteText
    )

    @Test
    fun roundTrip_preservesAllFields() {
        val original = sampleDraft()
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(original, restored)
    }

    @Test
    fun roundTrip_emptyCheckedStates() {
        val original = sampleDraft(checkedStates = emptyList())
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(original, restored)
    }

    @Test
    fun roundTrip_emptyStrings() {
        val original = sampleDraft(
            location = "",
            situationDescription = "",
            additionalNotes = "",
            generatedNoteText = ""
        )
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(original, restored)
    }

    @Test
    fun roundTrip_allChecked() {
        val original = sampleDraft(checkedStates = listOf(true, true, true, true))
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(4, restored!!.checkedStates.size)
        assertEquals(listOf(true, true, true, true), restored.checkedStates)
    }

    @Test
    fun roundTrip_allUnchecked() {
        val original = sampleDraft(checkedStates = listOf(false, false))
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(listOf(false, false), restored!!.checkedStates)
    }

    @Test
    fun jsonToDraft_invalidJson_returnsNull() {
        val result = jsonToDraft("id", "not json at all")
        assertNull(result)
    }

    @Test
    fun jsonToDraft_emptyJson_returnsNull() {
        val result = jsonToDraft("id", "")
        assertNull(result)
    }

    @Test
    fun jsonToDraft_missingFields_usesDefaults() {
        val json = """{"checkedStates":[true]}"""
        val result = jsonToDraft("id", json)

        assertEquals(listOf(true), result!!.checkedStates)
        assertEquals("", result.location)
        assertEquals("", result.situationDescription)
        assertEquals("", result.additionalNotes)
        assertEquals("", result.generatedNoteText)
    }

    @Test
    fun roundTrip_specialCharactersInText() {
        val original = sampleDraft(
            location = "ul. Żółwia 3/5, \"Łódź\"",
            situationDescription = "Notatka z dnia 01.01 — opis: <ważne>",
            additionalNotes = "Uwagi:\n- punkt 1\n- punkt 2"
        )
        val json = draftToJson(original)
        val restored = jsonToDraft(original.scenarioId, json)

        assertEquals(original, restored)
    }

    @Test
    fun scenarioId_isPassedThrough() {
        val original = sampleDraft(scenarioId = "urgent_child_abuse")
        val json = draftToJson(original)
        val restored = jsonToDraft("urgent_child_abuse", json)

        assertEquals("urgent_child_abuse", restored!!.scenarioId)
    }
}
