package pl.mikoch.asystentsocjalny.features.urgent

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.mikoch.asystentsocjalny.core.data.ActionRecommendationEngine
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.CaseLifecycleRules
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.data.DraftStore
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.NoteDraftBuilder
import pl.mikoch.asystentsocjalny.core.data.NoteSuggestionEngine
import pl.mikoch.asystentsocjalny.core.data.PdfDraftContent
import pl.mikoch.asystentsocjalny.core.data.PdfDraftGenerator
import pl.mikoch.asystentsocjalny.core.data.RiskAssessmentEngine
import pl.mikoch.asystentsocjalny.core.data.UrgentDraft
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseStatus
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import java.util.UUID
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgressCalculator
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus
import pl.mikoch.asystentsocjalny.features.urgent.model.toUi
import java.io.File

data class PdfReadiness(
    val enabled: Boolean,
    val reason: String
)

@HiltViewModel
class UrgentViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: KnowledgeRepository,
    private val draftStore: DraftStore,
    private val caseStore: CaseStore,
    private val documentStore: CaseDocumentStore
) : ViewModel() {

    val scenarios: List<UrgentScenarioUi> by lazy {
        repository.loadUrgentScenarios().map { it.toUi() }
    }

    fun scenarioById(id: String): UrgentScenarioUi? =
        scenarios.firstOrNull { it.id == id }

    // --- detail screen state ---

    private var currentScenarioId: String? = null
    private var currentCaseId: String? = null

    val activeCaseId: String? get() = currentCaseId
    val caseCanClose = mutableStateOf(false)

    val currentScenario: UrgentScenarioUi?
        get() = currentScenarioId?.let { scenarioById(it) }
    val checkedStates = mutableStateListOf<Boolean>()
    val location = mutableStateOf("")
    val situationDescription = mutableStateOf("")
    val additionalNotes = mutableStateOf("")
    val generatedNoteText = mutableStateOf("")
    val draftRestored = mutableStateOf(false)
    val lastGeneratedPdf = mutableStateOf<File?>(null)
    val runningNotes = mutableStateOf("")
    val stepNotes = mutableStateMapOf<Int, String>()
    val personsPresent = mutableStateOf("")
    val situationFlags = mutableStateListOf<String>()

    private var currentSteps: List<pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi> = emptyList()

    val progress = derivedStateOf {
        UrgentProgressCalculator.calculate(currentSteps, checkedStates.toList())
    }

    val riskAssessment = derivedStateOf {
        val p = progress.value
        RiskAssessmentEngine.assess(
            totalSteps = p.totalSteps,
            completedSteps = p.completedSteps,
            uncheckedCriticalCount = p.uncheckedCriticalSteps.size
        )
    }

    val recommendation = derivedStateOf {
        ActionRecommendationEngine.recommend(
            riskAssessment = riskAssessment.value,
            progress = progress.value,
            guidance = currentScenario?.guidance
        )
    }

    val noteSuggestions = derivedStateOf {
        val p = progress.value
        val completedTexts = currentSteps
            .filterIndexed { i, _ -> checkedStates.getOrElse(i) { false } }
            .map { it.text }
        val uncheckedCriticalTexts = p.uncheckedCriticalSteps.map { it.text }
        NoteSuggestionEngine.suggest(
            NoteSuggestionEngine.Input(
                completedStepTexts = completedTexts,
                uncheckedCriticalTexts = uncheckedCriticalTexts,
                riskLevel = riskAssessment.value.level,
                scenarioTitle = currentScenario?.title ?: "",
                situationFlags = situationFlags.toList(),
                additionalNotes = additionalNotes.value
            )
        )
    }

    val pdfReadiness = derivedStateOf {
        val p = progress.value
        val noteExists = generatedNoteText.value.isNotBlank()
        val descriptionExists = situationDescription.value.isNotBlank()
        val noCriticalLeft = p.uncheckedCriticalSteps.isEmpty()
        val notNeedsAttention = p.status != UrgentStatus.NEEDS_ATTENTION

        when {
            !noteExists -> PdfReadiness(false, "Najpierw wygeneruj notatkę")
            !descriptionExists -> PdfReadiness(false, "Uzupełnij opis sytuacji")
            !noCriticalLeft -> PdfReadiness(false, "Wykonaj wszystkie kroki krytyczne")
            !notNeedsAttention -> PdfReadiness(false, "Sprawa wymaga jeszcze uwagi")
            else -> PdfReadiness(true, "")
        }
    }

    fun generatePdf(): File {
        val context = appContext
        val title = currentScenario?.title ?: "notatka"
        val content = PdfDraftContent(
            scenarioTitle = title,
            date = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            caseStatus = progress.value.status.label,
            riskLevel = riskAssessment.value.level.label,
            recommendation = recommendation.value.summary,
            noteText = generatedNoteText.value
        )
        val file = PdfDraftGenerator.generate(context, content)
        lastGeneratedPdf.value = file
        val caseId = currentCaseId
        if (caseId != null) {
            viewModelScope.launch {
                documentStore.save(
                    CaseDocument(
                        documentId = UUID.randomUUID().toString(),
                        caseId = caseId,
                        type = DocumentType.PDF_DRAFT,
                        title = "${title} \u2014 PDF ${content.date}",
                        fileName = file.name,
                        textContent = "",
                        filePath = file.absolutePath,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
        return file
    }

    fun initDetailState(scenario: UrgentScenarioUi, caseId: String? = null) {
        val stateKey = caseId ?: scenario.id
        if (currentCaseId == caseId && currentScenarioId == scenario.id && caseId != null) return
        if (caseId == null && currentScenarioId == scenario.id && currentCaseId == null) return

        currentScenarioId = scenario.id
        currentCaseId = caseId
        currentSteps = scenario.steps
        draftRestored.value = false

        val draft = if (caseId != null) {
            runBlocking { draftStore.loadDraftForCase(caseId) }
        } else {
            runBlocking { draftStore.loadDraft(scenario.id) }
        }

        if (caseId != null) {
            val record = runBlocking { caseStore.loadCase(caseId) }
            caseCanClose.value = record != null && CaseLifecycleRules.canClose(record)
        } else {
            caseCanClose.value = false
        }

        checkedStates.clear()
        if (draft != null && draft.checkedStates.size == scenario.steps.size) {
            checkedStates.addAll(draft.checkedStates)
            location.value = draft.location
            situationDescription.value = draft.situationDescription
            additionalNotes.value = draft.additionalNotes
            generatedNoteText.value = draft.generatedNoteText
            runningNotes.value = draft.runningNotes
            stepNotes.clear()
            stepNotes.putAll(draft.stepNotes)
            personsPresent.value = draft.personsPresent
            situationFlags.clear()
            situationFlags.addAll(draft.situationFlags)
            draftRestored.value = true
        } else {
            checkedStates.addAll(List(scenario.steps.size) { false })
            location.value = ""
            situationDescription.value = ""
            additionalNotes.value = ""
            generatedNoteText.value = ""
            runningNotes.value = ""
            stepNotes.clear()
            personsPresent.value = ""
            situationFlags.clear()
        }
    }

    fun saveDraft() {
        val scenarioId = currentScenarioId ?: return
        val caseId = currentCaseId
        val draft = UrgentDraft(
            scenarioId = scenarioId,
            checkedStates = checkedStates.toList(),
            location = location.value,
            situationDescription = situationDescription.value,
            additionalNotes = additionalNotes.value,
            generatedNoteText = generatedNoteText.value,
            caseId = caseId ?: "",
            runningNotes = runningNotes.value,
            stepNotes = stepNotes.toMap(),
            personsPresent = personsPresent.value,
            situationFlags = situationFlags.toList()
        )
        viewModelScope.launch {
            if (caseId != null) {
                draftStore.saveDraftForCase(draft)
                updateCaseRecord(caseId, scenarioId)
                if (generatedNoteText.value.isNotBlank()) {
                    documentStore.save(
                        CaseDocument(
                            documentId = "note_$caseId",
                            caseId = caseId,
                            type = DocumentType.NOTE_DRAFT,
                            title = "${currentScenario?.title ?: "Sprawa"} \u2014 Notatka robocza",
                            fileName = "",
                            textContent = generatedNoteText.value,
                            filePath = "",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                draftStore.saveDraft(draft)
            }
        }
    }

    fun clearDraft() {
        val scenarioId = currentScenarioId ?: return
        val caseId = currentCaseId
        checkedStates.indices.forEach { checkedStates[it] = false }
        location.value = ""
        situationDescription.value = ""
        additionalNotes.value = ""
        generatedNoteText.value = ""
        draftRestored.value = false
        lastGeneratedPdf.value = null
        runningNotes.value = ""
        stepNotes.clear()
        personsPresent.value = ""
        situationFlags.clear()
        viewModelScope.launch {
            if (caseId != null) {
                draftStore.clearDraftForCase(caseId)
            } else {
                draftStore.clearDraft(scenarioId)
            }
        }
    }

    private suspend fun updateCaseRecord(caseId: String, scenarioId: String) {
        val existing = caseStore.loadCase(caseId) ?: return
        val risk = riskAssessment.value
        val prog = progress.value
        val status = when {
            prog.completedSteps == 0 -> CaseStatus.DRAFT
            prog.completedSteps == prog.totalSteps -> CaseStatus.READY_TO_CLOSE
            else -> CaseStatus.IN_PROGRESS
        }
        caseStore.saveCase(
            existing.copy(
                status = status,
                riskLevel = risk.level,
                updatedAt = System.currentTimeMillis(),
                isDraft = status == CaseStatus.DRAFT,
                locationPreview = location.value.take(50),
                hasNote = generatedNoteText.value.isNotBlank()
            )
        )
    }

    fun dismissDraftHint() {
        draftRestored.value = false
    }

    fun closeCase() {
        val caseId = currentCaseId ?: return
        viewModelScope.launch {
            val existing = caseStore.loadCase(caseId) ?: return@launch
            if (CaseLifecycleRules.canClose(existing)) {
                caseStore.saveCase(
                    existing.copy(
                        lifecycle = CaseLifecycle.CLOSED,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                caseCanClose.value = false
            }
        }
    }

    fun generateNote(scenario: UrgentScenarioUi) {
        val completedSteps = scenario.steps
            .filterIndexed { i, _ -> checkedStates.getOrElse(i) { false } }
            .map { it.text }
        val criticalCompleted = scenario.steps
            .filterIndexed { i, _ -> checkedStates.getOrElse(i) { false } }
            .filter { it.isCritical }
            .map { it.text }

        val draft = NoteDraftBuilder.build(
            scenarioTitle = scenario.title,
            scenarioDescription = scenario.description,
            completedSteps = completedSteps,
            criticalCompletedSteps = criticalCompleted,
            location = location.value,
            situationDescription = situationDescription.value,
            additionalNotes = additionalNotes.value,
            runningNotes = runningNotes.value,
            stepNotes = stepNotes.toMap(),
            personsPresent = personsPresent.value,
            situationFlags = situationFlags.toList()
        )
        generatedNoteText.value = NoteDraftBuilder.formatToText(draft)
        saveDraft()
    }
}
