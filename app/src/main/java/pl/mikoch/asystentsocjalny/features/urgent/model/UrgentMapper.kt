package pl.mikoch.asystentsocjalny.features.urgent.model

import pl.mikoch.asystentsocjalny.core.model.ChecklistStep
import pl.mikoch.asystentsocjalny.core.model.UrgentGuidance
import pl.mikoch.asystentsocjalny.core.model.UrgentScenario

fun UrgentScenario.toUi() = UrgentScenarioUi(
    id = id,
    title = title,
    description = description,
    steps = steps.map { it.toUi() },
    guidance = guidance?.toUi()
)

fun ChecklistStep.toUi() = ChecklistStepUi(
    id = id,
    text = text,
    isCritical = isCritical
)

fun UrgentGuidance.toUi() = GuidanceUi(
    notify = notify,
    documents = documents,
    doNotMiss = doNotMiss,
    escalationRequired = escalationRequired,
    escalationNote = escalationNote
)
