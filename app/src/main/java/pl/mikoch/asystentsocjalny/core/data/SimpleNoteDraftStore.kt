package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import android.content.SharedPreferences

class SimpleNoteDraftStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("simple_note_drafts", Context.MODE_PRIVATE)

    fun save(procedureId: String, text: String) {
        prefs.edit().putString("draft_$procedureId", text).apply()
    }

    fun load(procedureId: String): String? {
        return prefs.getString("draft_$procedureId", null)
    }

    fun clear(procedureId: String) {
        prefs.edit().remove("draft_$procedureId").apply()
    }
}
