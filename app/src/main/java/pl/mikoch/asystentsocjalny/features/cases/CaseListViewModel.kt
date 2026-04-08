package pl.mikoch.asystentsocjalny.features.cases

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.CaseLifecycleRules
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.data.DraftStore
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import java.util.UUID

class CaseListViewModel(application: Application) : AndroidViewModel(application) {

    private val caseStore = CaseStore(application)
    private val draftStore = DraftStore(application)
    private val documentStore = CaseDocumentStore(application)

    val cases = mutableStateOf<List<CaseRecord>>(emptyList())
    val lifecycleFilter = mutableStateOf<CaseLifecycle?>(null)

    fun loadCases() {
        viewModelScope.launch {
            val all = caseStore.loadAll().sortedByDescending { it.updatedAt }
            val filter = lifecycleFilter.value
            cases.value = if (filter != null) {
                all.filter { it.lifecycle == filter }
            } else {
                all
            }
        }
    }

    fun setFilter(lifecycle: CaseLifecycle?) {
        lifecycleFilter.value = lifecycle
        loadCases()
    }

    fun createCase(scenarioId: String, scenarioTitle: String): String {
        val caseId = UUID.randomUUID().toString()
        val record = CaseRecord(
            caseId = caseId,
            scenarioId = scenarioId,
            scenarioTitle = scenarioTitle,
            status = CaseStatus.DRAFT,
            riskLevel = RiskLevel.MEDIUM,
            updatedAt = System.currentTimeMillis(),
            isDraft = true,
            locationPreview = ""
        )
        viewModelScope.launch {
            caseStore.saveCase(record)
            loadCases()
        }
        return caseId
    }

    fun deleteCase(caseId: String) {
        viewModelScope.launch {
            caseStore.deleteCase(caseId)
            draftStore.clearDraftForCase(caseId)
            documentStore.deleteAllForCase(caseId)
            loadCases()
        }
    }

    fun closeCase(caseId: String) {
        viewModelScope.launch {
            val record = caseStore.loadCase(caseId) ?: return@launch
            if (CaseLifecycleRules.canClose(record)) {
                caseStore.saveCase(
                    record.copy(
                        lifecycle = CaseLifecycle.CLOSED,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                loadCases()
            }
        }
    }

    fun archiveCase(caseId: String) {
        viewModelScope.launch {
            val record = caseStore.loadCase(caseId) ?: return@launch
            if (CaseLifecycleRules.canArchive(record)) {
                caseStore.saveCase(
                    record.copy(
                        lifecycle = CaseLifecycle.ARCHIVED,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                loadCases()
            }
        }
    }

    fun restoreCase(caseId: String) {
        viewModelScope.launch {
            val record = caseStore.loadCase(caseId) ?: return@launch
            if (CaseLifecycleRules.canRestore(record)) {
                caseStore.saveCase(
                    record.copy(
                        lifecycle = CaseLifecycle.ACTIVE,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                loadCases()
            }
        }
    }
}
