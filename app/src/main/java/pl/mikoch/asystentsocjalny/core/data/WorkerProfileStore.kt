package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile

private val Context.workerProfileDataStore by preferencesDataStore(name = "worker_profile")

class WorkerProfileStore(private val context: Context) {

    private val keyFirstName = stringPreferencesKey("first_name")
    private val keyLastName = stringPreferencesKey("last_name")
    private val keyPosition = stringPreferencesKey("position")
    private val keyUnit = stringPreferencesKey("unit")
    private val keyPhone = stringPreferencesKey("phone")
    private val keyEmail = stringPreferencesKey("email")

    val profileFlow: Flow<WorkerProfile> = context.workerProfileDataStore.data.map { prefs ->
        WorkerProfile(
            firstName = prefs[keyFirstName].orEmpty(),
            lastName = prefs[keyLastName].orEmpty(),
            position = prefs[keyPosition].orEmpty(),
            unit = prefs[keyUnit].orEmpty(),
            phone = prefs[keyPhone].orEmpty(),
            email = prefs[keyEmail].orEmpty()
        )
    }

    suspend fun load(): WorkerProfile = profileFlow.first()

    suspend fun save(profile: WorkerProfile) {
        context.workerProfileDataStore.edit { prefs ->
            prefs[keyFirstName] = profile.firstName.trim()
            prefs[keyLastName] = profile.lastName.trim()
            prefs[keyPosition] = profile.position.trim()
            prefs[keyUnit] = profile.unit.trim()
            prefs[keyPhone] = profile.phone.trim()
            prefs[keyEmail] = profile.email.trim()
        }
    }
}
