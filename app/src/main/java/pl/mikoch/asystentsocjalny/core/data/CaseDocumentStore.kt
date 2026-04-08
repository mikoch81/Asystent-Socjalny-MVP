package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.DocumentType

private val Context.caseDocDataStore by preferencesDataStore(name = "case_documents")

class CaseDocumentStore(private val context: Context) {

    suspend fun save(document: CaseDocument) {
        val docKey = stringPreferencesKey("doc_${document.documentId}")
        val indexKey = stringPreferencesKey("docidx_${document.caseId}")
        context.caseDocDataStore.edit { prefs ->
            prefs[docKey] = documentToJson(document)
            val index = parseIndex(prefs[indexKey])
            if (document.documentId !in index) {
                prefs[indexKey] = JSONArray(index + document.documentId).toString()
            }
        }
    }

    suspend fun loadForCase(caseId: String): List<CaseDocument> {
        val prefs = context.caseDocDataStore.data.first()
        val indexKey = stringPreferencesKey("docidx_$caseId")
        val index = parseIndex(prefs[indexKey])
        return index.mapNotNull { docId ->
            val docKey = stringPreferencesKey("doc_$docId")
            prefs[docKey]?.let { jsonToDocument(it) }
        }.sortedByDescending { it.createdAt }
    }

    suspend fun delete(documentId: String, caseId: String) {
        val docKey = stringPreferencesKey("doc_$documentId")
        val indexKey = stringPreferencesKey("docidx_$caseId")
        context.caseDocDataStore.edit { prefs ->
            prefs.remove(docKey)
            val index = parseIndex(prefs[indexKey])
            prefs[indexKey] = JSONArray(index - documentId).toString()
        }
    }

    suspend fun deleteAllForCase(caseId: String) {
        val indexKey = stringPreferencesKey("docidx_$caseId")
        context.caseDocDataStore.edit { prefs ->
            val index = parseIndex(prefs[indexKey])
            index.forEach { docId ->
                prefs.remove(stringPreferencesKey("doc_$docId"))
            }
            prefs.remove(indexKey)
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

internal fun documentToJson(doc: CaseDocument): String {
    val obj = JSONObject()
    obj.put("documentId", doc.documentId)
    obj.put("caseId", doc.caseId)
    obj.put("type", doc.type.name)
    obj.put("title", doc.title)
    obj.put("fileName", doc.fileName)
    obj.put("textContent", doc.textContent)
    obj.put("filePath", doc.filePath)
    obj.put("createdAt", doc.createdAt)
    return obj.toString()
}

internal fun jsonToDocument(json: String): CaseDocument? {
    return try {
        val obj = JSONObject(json)
        CaseDocument(
            documentId = obj.getString("documentId"),
            caseId = obj.getString("caseId"),
            type = DocumentType.valueOf(obj.getString("type")),
            title = obj.getString("title"),
            fileName = obj.optString("fileName", ""),
            textContent = obj.optString("textContent", ""),
            filePath = obj.optString("filePath", ""),
            createdAt = obj.getLong("createdAt")
        )
    } catch (_: Exception) {
        null
    }
}
