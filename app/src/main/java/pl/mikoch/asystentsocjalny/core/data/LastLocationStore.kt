package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.lastLocationDataStore by preferencesDataStore(name = "last_location")

/**
 * Persists the last value the worker typed into the "Miejsce" (location) field
 * of the note generator. Stays on the device.
 */
class LastLocationStore(private val context: Context) {
    private val key = stringPreferencesKey("last_location")

    val flow: Flow<String> = context.lastLocationDataStore.data.map { it[key].orEmpty() }

    suspend fun load(): String = flow.first()

    suspend fun save(value: String) {
        context.lastLocationDataStore.edit { it[key] = value }
    }
}
