package pl.mikoch.asystentsocjalny.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.RecentItemsStore
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.Benefit
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.core.model.RecentItem

@HiltViewModel
class AsystentNavHostViewModel @Inject constructor(
    knowledgeRepository: KnowledgeRepository,
    private val recentItemsStore: RecentItemsStore,
    private val caseDocumentStore: CaseDocumentStore
) : ViewModel() {

    val procedures: List<Procedure> = knowledgeRepository.loadProcedures()
    val benefits: List<Benefit> = knowledgeRepository.loadBenefits()

    fun recordOpen(item: RecentItem) {
        viewModelScope.launch {
            recentItemsStore.recordOpen(item)
        }
    }

    fun saveCaseDocument(document: CaseDocument) {
        viewModelScope.launch {
            caseDocumentStore.save(document)
        }
    }
}