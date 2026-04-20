package pl.mikoch.asystentsocjalny.features.cases

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import java.io.File

@HiltViewModel
class CaseDocumentsViewModel @Inject constructor(
    private val documentStore: CaseDocumentStore,
    private val caseStore: CaseStore
) : ViewModel() {

    val documents = mutableStateOf<List<CaseDocument>>(emptyList())
    val caseRecord = mutableStateOf<CaseRecord?>(null)

    fun load(caseId: String) {
        viewModelScope.launch {
            documents.value = documentStore.loadForCase(caseId)
            caseRecord.value = caseStore.loadCase(caseId)
        }
    }

    fun delete(doc: CaseDocument, caseId: String) {
        viewModelScope.launch {
            if (doc.type == DocumentType.PDF_DRAFT && doc.filePath.isNotBlank()) {
                File(doc.filePath).delete()
            }
            documentStore.delete(doc.documentId, caseId)
            documents.value = documentStore.loadForCase(caseId)
        }
    }
}
