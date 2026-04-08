package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus

object CaseLifecycleRules {

    /** Stored lifecycle is always ACTIVE/CLOSED/ARCHIVED. */
    fun canClose(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ACTIVE

    fun canArchive(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.CLOSED

    fun canRestore(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ARCHIVED ||
                record.lifecycle == CaseLifecycle.CLOSED

    fun canEdit(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ACTIVE

    /**
     * Computes the display lifecycle dynamically.
     * READY_TO_CLOSE is never persisted — it is derived when:
     * - all steps complete (status == READY_TO_CLOSE),
     * - a note draft exists (hasNote == true).
     */
    fun displayLifecycle(record: CaseRecord): CaseLifecycle {
        if (record.lifecycle != CaseLifecycle.ACTIVE) return record.lifecycle
        return if (record.status == CaseStatus.READY_TO_CLOSE && record.hasNote) {
            CaseLifecycle.READY_TO_CLOSE
        } else {
            CaseLifecycle.ACTIVE
        }
    }
}
