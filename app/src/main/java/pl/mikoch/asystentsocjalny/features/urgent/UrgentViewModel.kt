package pl.mikoch.asystentsocjalny.features.urgent

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.NoteDraftBuilder
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgress
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentProgressCalculator
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentScenarioUi
import pl.mikoch.asystentsocjalny.features.urgent.model.UrgentStatus
import pl.mikoch.asystentsocjalny.features.urgent.model.toUi

class UrgentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KnowledgeRepository(application)

    val scenarios: List<UrgentScenarioUi> by lazy {
        repository.loadUrgentScenarios().map { it.toUi() }
    }

    fun scenarioById(id: String): UrgentScenarioUi? =
        scenarios.firstOrNull { it.id == id }

    // --- detail screen state ---

    private var currentScenarioId: String? = null
    val checkedStates = mutableStateListOf<Boolean>()
    val location = mutableStateOf("")
    val situationDescription = mutableStateOf("")
    val additionalNotes = mutableStateOf("")
    val generatedNoteText = mutableStateOf("")

    private var currentSteps: List<pl.mikoch.asystentsocjalny.features.urgent.model.ChecklistStepUi> = emptyList()

    val progress = derivedStateOf {
        UrgentProgressCalculator.calculate(currentSteps, checkedStates.toList())
    }

    fun initDetailState(scenario: UrgentScenarioUi) {
        if (currentScenarioId == scenario.id) return
        currentScenarioId = scenario.id
        currentSteps = scenario.steps
        checkedStates.clear()
        checkedStates.addAll(List(scenario.steps.size) { false })
        location.value = ""
        situationDescription.value = ""
        additionalNotes.value = ""
        generatedNoteText.value = ""
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
            additionalNotes = additionalNotes.value
        )
        generatedNoteText.value = NoteDraftBuilder.formatToText(draft)
    }
}
