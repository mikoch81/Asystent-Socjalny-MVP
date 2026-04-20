package pl.mikoch.asystentsocjalny.features.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.LastLocationStore
import pl.mikoch.asystentsocjalny.core.data.SimpleNoteDraftStore
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile

@HiltViewModel
class NotesViewModel @Inject constructor(
    workerProfileStore: WorkerProfileStore,
    private val lastLocationStore: LastLocationStore,
    private val draftStore: SimpleNoteDraftStore
) : ViewModel() {

    val profileFlow: Flow<WorkerProfile> = workerProfileStore.profileFlow
    val lastLocationFlow: Flow<String> = lastLocationStore.flow

    fun rememberLocation(value: String) {
        if (value.isBlank()) return
        viewModelScope.launch { lastLocationStore.save(value) }
    }

    fun loadDraft(procedureId: String): String? = draftStore.load(procedureId)

    fun saveDraft(procedureId: String, text: String) {
        draftStore.save(procedureId, text)
    }
}
