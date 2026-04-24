package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import pl.mikoch.asystentsocjalny.core.model.Benefit
import pl.mikoch.asystentsocjalny.core.model.ChecklistStep
import pl.mikoch.asystentsocjalny.core.model.ContactInfo
import pl.mikoch.asystentsocjalny.core.model.KnowledgeMeta
import pl.mikoch.asystentsocjalny.core.model.KnowledgeSource
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.core.model.UrgentGuidance
import pl.mikoch.asystentsocjalny.core.model.UrgentScenario
import java.io.File

class KnowledgeRepository(private val context: Context) {

    @Volatile
    private var lastExternalHitCount: Int = 0

    fun loadProcedures(): List<Procedure> = tryLoad(emptyList()) {
        val content = readJson("procedures.json")
        val root = JSONObject(content)
        parseProcedures(root.getJSONArray("procedures"))
    }

    fun loadBenefits(): List<Benefit> = tryLoad(emptyList()) {
        val content = readJson("benefits.json")
        val root = JSONObject(content)
        parseBenefits(root.getJSONArray("benefits"))
    }

    fun loadUrgentScenarios(): List<UrgentScenario> = tryLoad(emptyList()) {
        val content = readJson("procedures/urgent_scenarios.json")
        val root = JSONObject(content)
        parseUrgentScenarios(root.getJSONArray("scenarios"))
    }

    /**
     * Wczytuje metadane bazy wiedzy. Sprawdza wszystkie zbiory + meta.json
     * i zlicza, ile z nich pochodzi z katalogu zewnętrznego (OTA).
     */
    fun loadMeta(): KnowledgeMeta {
        lastExternalHitCount = 0
        val checks = listOf(
            readJsonOrNull("procedures.json"),
            readJsonOrNull("benefits.json"),
            readJsonOrNull("procedures/urgent_scenarios.json"),
            readJsonOrNull("meta.json")
        )
        val externalCount = checks.count { it?.fromExternal == true }
        val metaContent = checks[3]

        val (version, updatedAt) = parseMeta(metaContent?.text)
            ?: (FALLBACK_VERSION to FALLBACK_UPDATED)

        val source = if (externalCount > 0) KnowledgeSource.EXTERNAL else KnowledgeSource.BUNDLED
        return KnowledgeMeta(
            version = version,
            updatedAt = updatedAt,
            source = source,
            externalOverrideCount = externalCount
        )
    }

    private fun parseMeta(text: String?): Pair<String, String>? {
        if (text == null) return null
        return try {
            val obj = JSONObject(text)
            obj.optString("version", FALLBACK_VERSION) to obj.optString("updatedAt", FALLBACK_UPDATED)
        } catch (_: Exception) {
            null
        }
    }

    private fun <T> tryLoad(fallback: T, block: () -> T): T =
        try { block() } catch (_: Exception) { fallback }

    /**
     * Czyta JSON. Jeśli istnieje plik w katalogu zewnętrznym aplikacji
     * (`/Android/data/.../files/knowledge/`), używa go. To pozwala na OTA
     * bez uprawnień systemowych — plik podmienia się przez USB / Files.
     */
    private fun readJson(relativePath: String): String {
        val external = externalFile(relativePath)
        if (external != null && external.isFile && external.canRead()) {
            lastExternalHitCount++
            return external.readText(Charsets.UTF_8)
        }
        return readAsset("knowledge/$relativePath")
    }

    private data class JsonRead(val text: String, val fromExternal: Boolean)

    private fun readJsonOrNull(relativePath: String): JsonRead? {
        val external = externalFile(relativePath)
        if (external != null && external.isFile && external.canRead()) {
            return try {
                JsonRead(external.readText(Charsets.UTF_8), fromExternal = true)
            } catch (_: Exception) {
                null
            }
        }
        return try {
            JsonRead(readAsset("knowledge/$relativePath"), fromExternal = false)
        } catch (_: Exception) {
            null
        }
    }

    private fun externalFile(relativePath: String): File? {
        val base = context.getExternalFilesDir(EXTERNAL_DIR_NAME) ?: return null
        return File(base, relativePath)
    }

    private fun readAsset(path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }

    internal fun parseProcedures(array: JSONArray): List<Procedure> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Procedure(
                id = item.getString("id"),
                title = item.getString("title"),
                category = item.optString("category", ""),
                situation = item.getString("situation"),
                severity = item.getString("severity"),
                nowSteps = item.getJSONArray("nowSteps").toStringList(),
                notify = item.getJSONArray("notify").toStringList(),
                doNotMiss = item.getJSONArray("doNotMiss").toStringList(),
                legalBasis = item.getJSONArray("legalBasis").toStringList(),
                escalation = item.getString("escalation"),
                documents = item.getJSONArray("documents").toStringList(),
                relatedBenefits = item.optJSONArray("relatedBenefits")?.toStringList().orEmpty(),
                contact = item.optJSONObject("contact")?.toContactInfo(),
                legalUpdatedAt = item.optString("legalUpdatedAt", ""),
                legalReviewDueAt = item.optString("legalReviewDueAt", ""),
                legalValidationStatus = item.optString("legalValidationStatus", "Wymaga walidacji"),
                validatedBy = item.optString("validatedBy", "").ifBlank { null }
            )
        }
    }

    internal fun parseBenefits(array: JSONArray): List<Benefit> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Benefit(
                id = item.getString("id"),
                name = item.getString("name"),
                description = item.getString("description"),
                documents = item.getJSONArray("documents").toStringList(),
                conditions = if (item.has("conditions")) item.getJSONArray("conditions").toStringList() else emptyList(),
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

    internal fun parseUrgentScenarios(array: JSONArray): List<UrgentScenario> {
        return (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            val stepsArray = obj.getJSONArray("steps")
            val guidanceObj = if (obj.has("guidance")) obj.getJSONObject("guidance") else null
            UrgentScenario(
                id = obj.getString("id"),
                title = obj.getString("title"),
                description = obj.getString("description"),
                steps = (0 until stepsArray.length()).map { j ->
                    val step = stepsArray.getJSONObject(j)
                    ChecklistStep(
                        id = step.getString("id"),
                        text = step.getString("text"),
                        isCritical = step.getBoolean("isCritical")
                    )
                },
                guidance = guidanceObj?.let {
                    UrgentGuidance(
                        notify = it.getJSONArray("notify").toStringList(),
                        documents = it.getJSONArray("documents").toStringList(),
                        doNotMiss = it.getJSONArray("doNotMiss").toStringList(),
                        escalationRequired = it.getBoolean("escalationRequired"),
                        escalationNote = it.getString("escalationNote")
                    )
                }
            )
        }
    }

    companion object {
        private const val EXTERNAL_DIR_NAME = "knowledge"
        private const val FALLBACK_VERSION = "0.0.0"
        private const val FALLBACK_UPDATED = "—"
    }
}

private fun JSONArray.toStringList(): List<String> {
    return (0 until length()).map { index -> getString(index) }
}

private fun JSONObject.toContactInfo(): ContactInfo {
    return ContactInfo(
        unitName = optString("unitName"),
        phone = optString("phone"),
        hours = optString("hours"),
        address = optString("address")
    )
}
