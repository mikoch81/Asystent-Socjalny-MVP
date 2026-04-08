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
    val caseId: String = ""
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
            caseId = obj.optString("caseId", "")
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
            caseId = caseId
        )
    } catch (_: Exception) {
        null
    }
}
