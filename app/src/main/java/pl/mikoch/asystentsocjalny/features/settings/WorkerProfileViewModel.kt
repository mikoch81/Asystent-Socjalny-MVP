package pl.mikoch.asystentsocjalny.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.TextScale
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    private val store: WorkerProfileStore
) : ViewModel() {

    val profileFlow: Flow<WorkerProfile> = store.profileFlow

    fun save(profile: WorkerProfile, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            store.save(profile)
            onDone()
        }
    }

    fun setTextScale(scale: TextScale) {
        viewModelScope.launch { store.setTextScale(scale) }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { store.setHighContrast(enabled) }
    }
}
