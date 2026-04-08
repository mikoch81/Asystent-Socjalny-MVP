package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.RiskAssessment
import pl.mikoch.asystentsocjalny.core.model.RiskLevel

object RiskAssessmentEngine {

    fun assess(
        totalSteps: Int,
        completedSteps: Int,
        uncheckedCriticalCount: Int
    ): RiskAssessment {
        val reasons = mutableListOf<String>()

        if (uncheckedCriticalCount > 0) {
            reasons += "Niewykonane kroki krytyczne: $uncheckedCriticalCount"
        }

        if (totalSteps > 0 && completedSteps < totalSteps / 2) {
            reasons += "Postęp poniżej 50% ($completedSteps z $totalSteps)"
        }

        val level = when {
            uncheckedCriticalCount > 0 -> RiskLevel.HIGH
            totalSteps > 0 && completedSteps < totalSteps / 2 -> RiskLevel.MEDIUM
            totalSteps > 0 && completedSteps == totalSteps -> {
                reasons += "Wszystkie kroki wykonane"
                RiskLevel.LOW
            }
            else -> {
                reasons += "Postęp: $completedSteps z $totalSteps"
                RiskLevel.MEDIUM
            }
        }

        return RiskAssessment(level = level, reasons = reasons)
    }
}
