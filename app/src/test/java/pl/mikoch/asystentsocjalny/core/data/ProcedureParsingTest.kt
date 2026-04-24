package pl.mikoch.asystentsocjalny.core.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.mikoch.asystentsocjalny.core.model.ContactInfo
import pl.mikoch.asystentsocjalny.core.model.Procedure

class ProcedureParsingTest {

    @Test
    fun `parseProcedure maps related benefits and contact`() {
        val json = JSONObject(
            """
            {
              "id": "p1",
              "title": "Tytuł",
              "category": "Interwencyjne",
              "situation": "Sytuacja",
              "severity": "Wysoki",
              "nowSteps": ["A"],
              "notify": ["B"],
              "doNotMiss": ["C"],
              "legalBasis": ["D"],
              "escalation": "Tak",
              "documents": ["E"],
              "relatedBenefits": ["Zasiłek rodzinny"],
                            "legalUpdatedAt": "2026-04-20",
                            "legalReviewDueAt": "2026-06-30",
                            "legalValidationStatus": "Wymaga walidacji",
                                                        "validatedBy": "radca prawny",
              "contact": {
                "unitName": "MOPS Zgierz",
                "phone": "42 716 42 13",
                "hours": "pn-pt",
                "address": "Długa 56"
              }
            }
            """.trimIndent()
        )

        val procedure = parseProcedureItem(json)

        assertEquals(listOf("Zasiłek rodzinny"), procedure.relatedBenefits)
        assertEquals("MOPS Zgierz", procedure.contact?.unitName)
        assertEquals("42 716 42 13", procedure.contact?.phone)
        assertEquals("2026-04-20", procedure.legalUpdatedAt)
        assertEquals("2026-06-30", procedure.legalReviewDueAt)
        assertEquals("Wymaga walidacji", procedure.legalValidationStatus)
        assertEquals("radca prawny", procedure.validatedBy)
    }

    @Test
    fun `parseProcedure defaults when optional fields are missing`() {
        val json = JSONObject(
            """
            {
              "id": "p2",
              "title": "Tytuł",
              "category": "Interwencyjne",
              "situation": "Sytuacja",
              "severity": "Wysoki",
              "nowSteps": ["A"],
              "notify": ["B"],
              "doNotMiss": ["C"],
              "legalBasis": ["D"],
              "escalation": "Tak",
              "documents": ["E"]
            }
            """.trimIndent()
        )

        val procedure = parseProcedureItem(json)

        assertEquals(emptyList<String>(), procedure.relatedBenefits)
        assertNull(procedure.contact)
        assertEquals("", procedure.legalUpdatedAt)
        assertEquals("", procedure.legalReviewDueAt)
        assertEquals("Wymaga walidacji", procedure.legalValidationStatus)
        assertNull(procedure.validatedBy)
    }

    private fun parseProcedureItem(item: JSONObject): Procedure {
        fun parseStringList(key: String): List<String> {
            val arr = item.getJSONArray(key)
            return (0 until arr.length()).map { arr.getString(it) }
        }

        val contact = item.optJSONObject("contact")?.let {
            ContactInfo(
                unitName = it.optString("unitName"),
                phone = it.optString("phone"),
                hours = it.optString("hours"),
                address = it.optString("address")
            )
        }

        return Procedure(
            id = item.getString("id"),
            title = item.getString("title"),
            category = item.optString("category", ""),
            situation = item.getString("situation"),
            severity = item.getString("severity"),
            nowSteps = parseStringList("nowSteps"),
            notify = parseStringList("notify"),
            doNotMiss = parseStringList("doNotMiss"),
            legalBasis = parseStringList("legalBasis"),
            escalation = item.getString("escalation"),
            documents = parseStringList("documents"),
            relatedBenefits = item.optJSONArray("relatedBenefits")?.let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            }.orEmpty(),
            contact = contact,
            legalUpdatedAt = item.optString("legalUpdatedAt", ""),
            legalReviewDueAt = item.optString("legalReviewDueAt", ""),
            legalValidationStatus = item.optString("legalValidationStatus", "Wymaga walidacji"),
            validatedBy = item.optString("validatedBy", "").ifBlank { null }
        )
    }
}
