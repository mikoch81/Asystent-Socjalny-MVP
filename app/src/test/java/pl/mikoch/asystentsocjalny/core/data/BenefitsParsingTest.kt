package pl.mikoch.asystentsocjalny.core.data

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.Benefit

class BenefitsParsingTest {

    @Test
    fun `parseBenefit maps all fields correctly`() {
        val json = JSONObject("""
        {
            "id": "test-benefit",
            "name": "Zasiłek testowy",
            "description": "Opis testowy",
            "documents": ["Dowód osobisty", "Zaświadczenie"],
            "conditions": ["Dochód poniżej kryterium", "Wywiad środowiskowy"],
            "note": "Uwaga testowa",
            "category": "Zasiłki",
            "procedure": "Procedura testowa",
            "legalUpdatedAt": "2026-04-20",
            "legalReviewDueAt": "2026-06-30",
            "legalValidationStatus": "Wymaga walidacji",
            "validatedBy": "radca prawny"
        }
        """.trimIndent())

        val benefit = parseBenefitItem(json)

        assertEquals("test-benefit", benefit.id)
        assertEquals("Zasiłek testowy", benefit.name)
        assertEquals("Opis testowy", benefit.description)
        assertEquals(listOf("Dowód osobisty", "Zaświadczenie"), benefit.documents)
        assertEquals(listOf("Dochód poniżej kryterium", "Wywiad środowiskowy"), benefit.conditions)
        assertEquals("Uwaga testowa", benefit.note)
        assertEquals("Zasiłki", benefit.category)
        assertEquals("Procedura testowa", benefit.procedure)
        assertEquals("2026-04-20", benefit.legalUpdatedAt)
        assertEquals("2026-06-30", benefit.legalReviewDueAt)
        assertEquals("Wymaga walidacji", benefit.legalValidationStatus)
        assertEquals("radca prawny", benefit.validatedBy)
    }

    @Test
    fun `parseBenefit handles missing conditions field`() {
        val json = JSONObject("""
        {
            "id": "no-cond",
            "name": "Bez warunków",
            "description": "Opis",
            "documents": [],
            "note": "Uwaga"
        }
        """.trimIndent())

        val benefit = parseBenefitItem(json)

        assertEquals(emptyList<String>(), benefit.conditions)
    }

    @Test
    fun `parseBenefit preserves empty documents list`() {
        val json = JSONObject("""
        {
            "id": "empty-docs",
            "name": "Test",
            "description": "Opis",
            "documents": [],
            "conditions": ["Warunek"],
            "note": "Uwaga"
        }
        """.trimIndent())

        val benefit = parseBenefitItem(json)

        assertEquals(emptyList<String>(), benefit.documents)
        assertEquals(listOf("Warunek"), benefit.conditions)
    }

    @Test
    fun `parseBenefit uses defaults when category and procedure are missing`() {
        val json = JSONObject("""
        {
            "id": "defaults",
            "name": "Domyślny",
            "description": "Opis",
            "documents": [],
            "conditions": [],
            "note": "Uwaga"
        }
        """.trimIndent())

        val benefit = parseBenefitItem(json)

        assertEquals("Inne", benefit.category)
        assertEquals(null, benefit.procedure)
        assertEquals("", benefit.legalUpdatedAt)
        assertEquals("", benefit.legalReviewDueAt)
        assertEquals("Wymaga walidacji", benefit.legalValidationStatus)
        assertEquals(null, benefit.validatedBy)
    }

    private fun parseBenefitItem(item: JSONObject): Benefit {
        val conditions = if (item.has("conditions")) {
            val arr = item.getJSONArray("conditions")
            (0 until arr.length()).map { arr.getString(it) }
        } else {
            emptyList()
        }
        return Benefit(
            id = item.getString("id"),
            name = item.getString("name"),
            description = item.getString("description"),
            documents = item.getJSONArray("documents").let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            },
            conditions = conditions,
            note = item.getString("note"),
            category = item.optString("category", "Inne"),
            procedure = item.optString("procedure", "").ifBlank { null },
            legalUpdatedAt = item.optString("legalUpdatedAt", ""),
            legalReviewDueAt = item.optString("legalReviewDueAt", ""),
            legalValidationStatus = item.optString("legalValidationStatus", "Wymaga walidacji"),
            validatedBy = item.optString("validatedBy", "").ifBlank { null }
        )
    }
}
