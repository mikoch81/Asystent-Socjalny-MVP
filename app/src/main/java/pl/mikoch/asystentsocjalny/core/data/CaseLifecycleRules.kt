package pl.mikoch.asystentsocjalny.core.data

import pl.mikoch.asystentsocjalny.core.model.CaseLifecycle
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.CaseStatus

object CaseLifecycleRules {

    fun canClose(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ACTIVE ||
                record.lifecycle == CaseLifecycle.READY_TO_CLOSE

    fun canArchive(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.CLOSED

    fun canRestore(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ARCHIVED ||
                record.lifecycle == CaseLifecycle.CLOSED

    fun canEdit(record: CaseRecord): Boolean =
        record.lifecycle == CaseLifecycle.ACTIVE ||
                record.lifecycle == CaseLifecycle.READY_TO_CLOSE

    fun autoLifecycle(record: CaseRecord): CaseLifecycle {
        if (record.lifecycle == CaseLifecycle.CLOSED ||
            record.lifecycle == CaseLifecycle.ARCHIVED
        ) {
            return record.lifecycle
        }
        return if (record.status == CaseStatus.READY_TO_CLOSE) {
            CaseLifecycle.READY_TO_CLOSE
        } else {
            CaseLifecycle.ACTIVE
        }
    }
}
