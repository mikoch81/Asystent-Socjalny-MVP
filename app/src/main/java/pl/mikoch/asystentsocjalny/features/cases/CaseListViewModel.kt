package pl.mikoch.asystentsocjalny.features.cases

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.data.DraftStore
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.RiskLevel
import java.util.UUID

class CaseListViewModel(application: Application) : AndroidViewModel(application) {

    private val caseStore = CaseStore(application)
    private val draftStore = DraftStore(application)

    val cases = mutableStateOf<List<CaseRecord>>(emptyList())

    fun loadCases() {
        viewModelScope.launch {
            cases.value = caseStore.loadAll().sortedByDescending { it.updatedAt }
        }
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
            loadCases()
        }
    }
}
