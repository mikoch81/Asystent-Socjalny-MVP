package pl.mikoch.asystentsocjalny.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.RecentItemsStore
import pl.mikoch.asystentsocjalny.core.model.KnowledgeMeta
import pl.mikoch.asystentsocjalny.core.model.KnowledgeSource
import pl.mikoch.asystentsocjalny.core.model.RecentItem

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recentItemsStore: RecentItemsStore,
    knowledgeRepository: KnowledgeRepository
) : ViewModel() {

    val recent: StateFlow<List<RecentItem>> = recentItemsStore.recentFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pinned: StateFlow<List<RecentItem>> = recentItemsStore.pinnedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Metadane bazy wiedzy. Wczytywane raz na start ViewModelu — pliki JSON
     * nie zmieniają się w trakcie sesji.
     */
    val knowledgeMeta: KnowledgeMeta = knowledgeRepository.loadMeta()

    fun togglePin(item: RecentItem) {
        viewModelScope.launch { recentItemsStore.togglePin(item) }
    }
}

