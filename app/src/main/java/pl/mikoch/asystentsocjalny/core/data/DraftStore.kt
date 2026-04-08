package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.draftDataStore by preferencesDataStore(name = "urgent_drafts")

data class UrgentDraft(
    val scenarioId: String,
    val checkedStates: List<Boolean>,
    val location: String,
    val situationDescription: String,
    val additionalNotes: String,
    val generatedNoteText: String,
    val caseId: String = "",
    val runningNotes: String = "",
    val stepNotes: Map<Int, String> = emptyMap(),
    val personsPresent: String = "",
    val situationFlags: List<String> = emptyList()
)

class DraftStore(private val context: Context) {

    suspend fun saveDraft(draft: UrgentDraft) {
        val key = stringPreferencesKey("draft_${draft.scenarioId}")
        val json = draftToJson(draft)
        context.draftDataStore.edit { prefs ->
            prefs[key] = json
        }
    }

    suspend fun loadDraft(scenarioId: String): UrgentDraft? {
        val key = stringPreferencesKey("draft_$scenarioId")
        val json = context.draftDataStore.data
            .map { prefs -> prefs[key] }
            .first()
        return json?.let { jsonToDraft(scenarioId, it) }
    }

    suspend fun clearDraft(scenarioId: String) {
        val key = stringPreferencesKey("draft_$scenarioId")
        context.draftDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    // --- case-keyed methods ---

    suspend fun saveDraftForCase(draft: UrgentDraft) {
        require(draft.caseId.isNotBlank()) { "caseId must not be blank" }
        val key = stringPreferencesKey("cdraft_${draft.caseId}")
        context.draftDataStore.edit { prefs ->
            prefs[key] = draftToJson(draft)
        }
    }

    suspend fun loadDraftForCase(caseId: String): UrgentDraft? {
        val key = stringPreferencesKey("cdraft_$caseId")
        val json = context.draftDataStore.data
            .map { prefs -> prefs[key] }
            .first()
        return json?.let { jsonToCaseDraft(caseId, it) }
    }

    suspend fun clearDraftForCase(caseId: String) {
        val key = stringPreferencesKey("cdraft_$caseId")
        context.draftDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}

internal fun draftToJson(draft: UrgentDraft): String {
    val obj = JSONObject()
    obj.put("checkedStates", JSONArray(draft.checkedStates.map { it }))
    obj.put("location", draft.location)
    obj.put("situationDescription", draft.situationDescription)
    obj.put("additionalNotes", draft.additionalNotes)
    obj.put("generatedNoteText", draft.generatedNoteText)
    obj.put("runningNotes", draft.runningNotes)
    obj.put("personsPresent", draft.personsPresent)
    obj.put("situationFlags", JSONArray(draft.situationFlags))
    val stepNotesObj = JSONObject()
    draft.stepNotes.forEach { (k, v) -> stepNotesObj.put(k.toString(), v) }
    obj.put("stepNotes", stepNotesObj)
    if (draft.caseId.isNotBlank()) {
        obj.put("caseId", draft.caseId)
        obj.put("scenarioId", draft.scenarioId)
    }
    return obj.toString()
}

internal fun jsonToDraft(scenarioId: String, json: String): UrgentDraft? {
    return try {
        val obj = JSONObject(json)
        val arr = obj.getJSONArray("checkedStates")
        val checked = (0 until arr.length()).map { arr.getBoolean(it) }
        UrgentDraft(
            scenarioId = scenarioId,
            checkedStates = checked,
            location = obj.optString("location", ""),
            situationDescription = obj.optString("situationDescription", ""),
            additionalNotes = obj.optString("additionalNotes", ""),
            generatedNoteText = obj.optString("generatedNoteText", ""),
            caseId = obj.optString("caseId", ""),
            runningNotes = obj.optString("runningNotes", ""),
            personsPresent = obj.optString("personsPresent", ""),
            situationFlags = parseStringArray(obj.optJSONArray("situationFlags")),
            stepNotes = parseStepNotes(obj.optJSONObject("stepNotes"))
        )
    } catch (_: Exception) {
        null
    }
}

internal fun jsonToCaseDraft(caseId: String, json: String): UrgentDraft? {
    return try {
        val obj = JSONObject(json)
        val arr = obj.getJSONArray("checkedStates")
        val checked = (0 until arr.length()).map { arr.getBoolean(it) }
        val scenarioId = obj.optString("scenarioId", "")
        UrgentDraft(
            scenarioId = scenarioId,
            checkedStates = checked,
            location = obj.optString("location", ""),
            situationDescription = obj.optString("situationDescription", ""),
            additionalNotes = obj.optString("additionalNotes", ""),
            generatedNoteText = obj.optString("generatedNoteText", ""),
            caseId = caseId,
            runningNotes = obj.optString("runningNotes", ""),
            personsPresent = obj.optString("personsPresent", ""),
            situationFlags = parseStringArray(obj.optJSONArray("situationFlags")),
            stepNotes = parseStepNotes(obj.optJSONObject("stepNotes"))
        )
    } catch (_: Exception) {
        null
    }
}

private fun parseStringArray(arr: JSONArray?): List<String> {
    if (arr == null) return emptyList()
    return (0 until arr.length()).map { arr.getString(it) }
}

private fun parseStepNotes(obj: JSONObject?): Map<Int, String> {
    if (obj == null) return emptyMap()
    val map = mutableMapOf<Int, String>()
    obj.keys().forEach { key ->
        val value = obj.optString(key, "")
        if (value.isNotBlank()) {
            key.toIntOrNull()?.let { map[it] = value }
        }
    }
    return map
}
