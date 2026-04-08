package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

private val Context.caseDataStore by preferencesDataStore(name = "case_records")

private val CASE_INDEX_KEY = stringPreferencesKey("case_index")

class CaseStore(private val context: Context) {

    suspend fun saveCase(record: CaseRecord) {
        val key = stringPreferencesKey("case_${record.caseId}")
        context.caseDataStore.edit { prefs ->
            prefs[key] = caseToJson(record)
            val index = parseIndex(prefs[CASE_INDEX_KEY])
            if (record.caseId !in index) {
                prefs[CASE_INDEX_KEY] = JSONArray(index + record.caseId).toString()
            }
        }
    }

    suspend fun loadAll(): List<CaseRecord> {
        val prefs = context.caseDataStore.data.first()
        val index = parseIndex(prefs[CASE_INDEX_KEY])
        return index.mapNotNull { caseId ->
            val key = stringPreferencesKey("case_$caseId")
            prefs[key]?.let { jsonToCase(it) }
        }
    }

    suspend fun loadCase(caseId: String): CaseRecord? {
        val key = stringPreferencesKey("case_$caseId")
        val json = context.caseDataStore.data.map { it[key] }.first()
        return json?.let { jsonToCase(it) }
    }

    suspend fun deleteCase(caseId: String) {
        val key = stringPreferencesKey("case_$caseId")
        context.caseDataStore.edit { prefs ->
            prefs.remove(key)
            val index = parseIndex(prefs[CASE_INDEX_KEY])
            prefs[CASE_INDEX_KEY] = JSONArray(index - caseId).toString()
        }
    }

    private fun parseIndex(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

internal fun caseToJson(record: CaseRecord): String {
    val obj = JSONObject()
    obj.put("caseId", record.caseId)
    obj.put("scenarioId", record.scenarioId)
    obj.put("scenarioTitle", record.scenarioTitle)
    obj.put("status", record.status.name)
    obj.put("riskLevel", record.riskLevel.name)
    obj.put("updatedAt", record.updatedAt)
    obj.put("isDraft", record.isDraft)
    obj.put("locationPreview", record.locationPreview)
    return obj.toString()
}

internal fun jsonToCase(json: String): CaseRecord? {
    return try {
        val obj = JSONObject(json)
        CaseRecord(
            caseId = obj.getString("caseId"),
            scenarioId = obj.getString("scenarioId"),
            scenarioTitle = obj.getString("scenarioTitle"),
            status = CaseStatus.valueOf(obj.getString("status")),
            riskLevel = RiskLevel.valueOf(obj.getString("riskLevel")),
            updatedAt = obj.getLong("updatedAt"),
            isDraft = obj.optBoolean("isDraft", true),
            locationPreview = obj.optString("locationPreview", "")
        )
    } catch (_: Exception) {
        null
    }
}
