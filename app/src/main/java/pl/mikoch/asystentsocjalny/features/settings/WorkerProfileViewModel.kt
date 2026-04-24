package pl.mikoch.asystentsocjalny.features.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.model.KnowledgeMeta
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.TextScale
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    private val store: WorkerProfileStore,
    knowledgeRepository: KnowledgeRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    val profileFlow: Flow<WorkerProfile> = store.profileFlow
    val knowledgeMeta: KnowledgeMeta = knowledgeRepository.loadMeta()
    val otaDirectoryPath: String =
        context.getExternalFilesDir("knowledge")?.absolutePath
            ?: "/Android/data/pl.mikoch.asystentsocjalny/files/knowledge"
    val otaSupportedFiles: List<String> = listOf(
        "meta.json",
        "procedures.json",
        "benefits.json",
        "procedures/urgent_scenarios.json"
    )

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
